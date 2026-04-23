package com.carbonfootprint.service;

import com.carbonfootprint.dto.AiAnalysisDTO;
import com.carbonfootprint.dto.CarbonPredictionDTO;
import com.carbonfootprint.dto.CarbonPredictionHistoryDTO;
import com.carbonfootprint.entity.FootprintSummary;
import com.carbonfootprint.entity.User;
import com.carbonfootprint.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CarbonAiAnalysisService {

    private final ReportService reportService;
    private final CarbonPredictionService predictionService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Value("${ai.provider:zhipu}")
    private String provider;

    @Value("${ai.base-url:https://open.bigmodel.cn/api/paas/v4}")
    private String baseUrl;

    @Value("${ai.api-key:}")
    private String apiKey;

    @Value("${ai.model:glm-4-flash}")
    private String model;

    @Value("${ai.timeout-seconds:45}")
    private int timeoutSeconds;

    @Value("${ai.cache-minutes:10}")
    private int cacheMinutes;

    @Value("${ai.failure-cooldown-seconds:180}")
    private int failureCooldownSeconds;

    private final ConcurrentHashMap<String, CachedAnalysis> remoteAnalysisCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, LocalDateTime> remoteRetryAfter = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Object> remoteAnalysisLocks = new ConcurrentHashMap<>();

    public AiAnalysisDTO analyze(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));

        FootprintSummary summary = reportService.generateFootprintSummary(
                userId,
                FootprintSummary.Period.MONTHLY,
                java.time.LocalDate.now());

        CarbonPredictionDTO prediction = predictionService.predictNextMonth(userId);
        List<CarbonPredictionHistoryDTO> history = predictionService.getPredictionHistory(userId);

        if (isLocalProvider()) {
            return buildLocalAnalysis(user, summary, prediction, history);
        }

        try {
            return buildRemoteAnalysisWithCache(userId, user, summary, prediction, history);
        } catch (Exception ex) {
            log.warn("AI analysis provider {} failed for user {}. Falling back to local analysis.", provider, userId, ex);
            return buildLocalAnalysis(user, summary, prediction, history);
        }
    }

    private AiAnalysisDTO buildRemoteAnalysisWithCache(Long userId,
                                                       User user,
                                                       FootprintSummary summary,
                                                       CarbonPredictionDTO prediction,
                                                       List<CarbonPredictionHistoryDTO> history) {
        String prompt = buildPrompt(user, summary, prediction, history);
        String cacheKey = buildCacheKey(userId, prompt);
        LocalDateTime now = LocalDateTime.now();

        AiAnalysisDTO cachedAnalysis = getCachedAnalysis(cacheKey, now);
        if (cachedAnalysis != null) {
            log.info("AI analysis cache hit for user {} using provider {}.", userId, provider);
            return cachedAnalysis;
        }

        if (isInRetryCooldown(cacheKey, now)) {
            log.info("AI analysis provider {} is in cooldown for user {}. Returning local analysis.", provider, userId);
            return buildLocalAnalysis(user, summary, prediction, history);
        }

        Object lock = remoteAnalysisLocks.computeIfAbsent(cacheKey, key -> new Object());
        synchronized (lock) {
            now = LocalDateTime.now();
            cachedAnalysis = getCachedAnalysis(cacheKey, now);
            if (cachedAnalysis != null) {
                log.info("AI analysis cache hit after lock for user {} using provider {}.", userId, provider);
                return cachedAnalysis;
            }

            if (isInRetryCooldown(cacheKey, now)) {
                log.info("AI analysis provider {} is still in cooldown after lock for user {}.", provider, userId);
                return buildLocalAnalysis(user, summary, prediction, history);
            }

            try {
                AiAnalysisDTO analysis = buildRemoteAnalysis(user, summary, prediction, history, prompt);
                cacheRemoteAnalysis(cacheKey, analysis);
                remoteRetryAfter.remove(cacheKey);
                return analysis;
            } catch (Exception ex) {
                remoteRetryAfter.put(cacheKey, now.plusSeconds(Math.max(30, failureCooldownSeconds)));
                throw ex;
            }
        }
    }

    private AiAnalysisDTO buildRemoteAnalysis(User user,
                                              FootprintSummary summary,
                                              CarbonPredictionDTO prediction,
                                              List<CarbonPredictionHistoryDTO> history) {
        String prompt = buildPrompt(user, summary, prediction, history);
        return buildRemoteAnalysis(user, summary, prediction, history, prompt);
    }

    private AiAnalysisDTO buildRemoteAnalysis(User user,
                                              FootprintSummary summary,
                                              CarbonPredictionDTO prediction,
                                              List<CarbonPredictionHistoryDTO> history,
                                              String prompt) {
        validateConfiguration();

        String content = callModel(prompt);
        AiAnalysisDTO analysis = parseAnalysis(content, model);

        if (analysis.getGeneratedAt() == null) {
            analysis.setGeneratedAt(LocalDateTime.now());
        }
        if (analysis.getModel() == null || analysis.getModel().isBlank()) {
            analysis.setModel(model);
        }
        if (analysis.getSource() == null || analysis.getSource().isBlank()) {
            analysis.setSource("AI");
        }

        return analysis;
    }

    private AiAnalysisDTO getCachedAnalysis(String cacheKey, LocalDateTime now) {
        CachedAnalysis cached = remoteAnalysisCache.get(cacheKey);
        if (cached == null) {
            return null;
        }

        if (cached.expiresAt.isBefore(now)) {
            remoteAnalysisCache.remove(cacheKey, cached);
            return null;
        }

        return cached.analysis;
    }

    private boolean isInRetryCooldown(String cacheKey, LocalDateTime now) {
        LocalDateTime retryAfter = remoteRetryAfter.get(cacheKey);
        if (retryAfter == null) {
            return false;
        }

        if (retryAfter.isBefore(now) || retryAfter.isEqual(now)) {
            remoteRetryAfter.remove(cacheKey, retryAfter);
            return false;
        }

        return true;
    }

    private void cacheRemoteAnalysis(String cacheKey, AiAnalysisDTO analysis) {
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(Math.max(1, cacheMinutes));
        remoteAnalysisCache.put(cacheKey, new CachedAnalysis(analysis, expiresAt));
    }

    private String buildCacheKey(Long userId, String prompt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(prompt.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(hash.length * 2);
            for (byte value : hash) {
                hex.append(String.format("%02x", value));
            }
            return userId + ":" + provider + ":" + model + ":" + hex;
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("无法计算 AI 缓存键", ex);
        }
    }

    private void validateConfiguration() {
        if (baseUrl == null || baseUrl.isBlank()) {
            throw new IllegalStateException("AI 服务未配置，请设置 AI_BASE_URL");
        }
        if (requiresApiKey() && (apiKey == null || apiKey.isBlank())) {
            throw new IllegalStateException("AI 服务未配置，请设置 AI_API_KEY 和 AI_BASE_URL");
        }
    }

    private String buildPrompt(User user,
                               FootprintSummary summary,
                               CarbonPredictionDTO prediction,
                               List<CarbonPredictionHistoryDTO> history) throws RuntimeException {
        try {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("userName", user.getName());
            payload.put("role", user.getRole().name());
            Map<String, Object> monthlySummary = new LinkedHashMap<>();
            monthlySummary.put("totalEmission", summary.getTotalEmission());
            monthlySummary.put("transportEmission", summary.getTransportEmission());
            monthlySummary.put("dietEmission", summary.getDietEmission());
            monthlySummary.put("electricityEmission", summary.getElectricityEmission());
            monthlySummary.put("periodStartDate", summary.getPeriodStartDate());
            monthlySummary.put("periodEndDate", summary.getPeriodEndDate());
            payload.put("monthlySummary", monthlySummary);

            Map<String, Object> predictionPayload = new LinkedHashMap<>();
            predictionPayload.put("predictionDate", prediction.getPredictionDate());
            predictionPayload.put("predictedEmission", prediction.getPredictedEmission());
            predictionPayload.put("confidence", prediction.getConfidence());
            predictionPayload.put("trend", prediction.getTrend());
            predictionPayload.put("suggestion", prediction.getSuggestion());
            payload.put("prediction", predictionPayload);
            payload.put("history", history.stream().limit(8).collect(Collectors.toList()));

            String contextJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(payload);

            return "你是企业级碳足迹诊断与预测专家。请基于给定数据输出 JSON，且只输出 JSON，不要输出 Markdown、注释或额外解释。\n"
                    + "作为大赛重点的 AI 诊断与预测模块，请务必执行：\n"
                    + "1. 【深度溯源分析】在 insights 中必须有一项 title 为「核心根因溯源」，分析当前最大排放源及习惯成因；\n"
                    + "2. 【趋势预测判断】在 insights 中有一项 title 为「未来趋势预测」，结合历史数据预测下月走势及潜在风险点；\n"
                    + "3. 【任务闭环建议】在 recommendations 中提供具体、可量化（如含有减排量）的无痛减排打卡任务建议。\n"
                    + "JSON 必须包含以下字段：headline, summary, riskLevel, confidence, insights, recommendations, nextActions, source。\n"
                    + "其中 insights 是对象数组，每个对象包含 title 和 text；recommendations 和 nextActions 是字符串数组；riskLevel 只能是 LOW, MEDIUM, HIGH 之一；confidence 是 0 到 100 之间的数字；source 固定返回 AI。\n"
                    + "请用简体中文输出，并尽量给出严谨的数据推理与切实可行的建议。\n\n"
                    + "数据如下：\n"
                    + contextJson;
        } catch (IOException ex) {
            throw new RuntimeException("构建 AI 提示词失败", ex);
        }
    }

    private String callModel(String prompt) {
        try {
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("model", model);
            body.put("temperature", 0.2);
            body.put("messages", List.of(
                Map.of("role", "system", "content", "你是专业的碳排放分析助手，只输出严格 JSON。"),
                Map.of("role", "user", "content", prompt)));

            String endpoint = buildEndpoint();
            if (isOllamaProvider()) {
                body.put("format", "json");
                body.put("stream", false);
            }

            String requestBody = objectMapper.writeValueAsString(body);

            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder(URI.create(endpoint))
                .header("Content-Type", "application/json")
                .timeout(java.time.Duration.ofSeconds(timeoutSeconds))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8));

            if (requiresApiKey()) {
                requestBuilder.header("Authorization", "Bearer " + apiKey);
            }

            if (isOpenRouterProvider()) {
                requestBuilder.header("HTTP-Referer", "http://localhost");
                requestBuilder.header("X-Title", "Carbon Footprint Tracker");
            }

            HttpRequest request = requestBuilder.build();

                HttpClient httpClient = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
                    .build();

                HttpResponse<String> response = httpClient
                    .send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IllegalStateException("AI 服务调用失败，状态码: " + response.statusCode() + ", 响应: " + response.body());
            }

            JsonNode root = objectMapper.readTree(response.body());
            JsonNode contentNode = isOllamaProvider()
                    ? root.path("message").path("content")
                    : root.path("choices").path(0).path("message").path("content");
            if (contentNode.isMissingNode() || contentNode.asText().isBlank()) {
                throw new IllegalStateException("AI 服务返回内容为空");
            }

            return contentNode.asText();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            log.warn("AI request interrupted", ex);
            throw new IllegalStateException("AI 服务调用被中断", ex);
        } catch (IOException ex) {
            log.warn("AI request IO failure: {}", ex.toString(), ex);
            throw new IllegalStateException("AI 服务调用失败", ex);
        }
    }

    private AiAnalysisDTO parseAnalysis(String content, String modelName) {
        try {
            String normalizedContent = content.trim();
            if (normalizedContent.startsWith("```")) {
            normalizedContent = normalizedContent.replaceFirst("^```(?:json)?\\s*", "");
            normalizedContent = normalizedContent.replaceFirst("\\s*```$", "");
            }

            JsonNode root = objectMapper.readTree(normalizedContent);

            List<AiAnalysisDTO.Insight> insights = root.path("insights").isArray()
                ? objectMapper.convertValue(
                root.path("insights"),
                objectMapper.getTypeFactory().constructCollectionType(List.class, AiAnalysisDTO.Insight.class))
                : List.of();

            List<String> recommendations = root.path("recommendations").isArray()
                ? objectMapper.convertValue(
                root.path("recommendations"),
                objectMapper.getTypeFactory().constructCollectionType(List.class, String.class))
                : List.of();

            List<String> nextActions = root.path("nextActions").isArray()
                ? objectMapper.convertValue(
                root.path("nextActions"),
                objectMapper.getTypeFactory().constructCollectionType(List.class, String.class))
                : List.of();

            return new AiAnalysisDTO(
                    modelName,
                    LocalDateTime.now(),
                    root.path("headline").asText("AI 分析完成"),
                    root.path("summary").asText("已根据历史数据生成分析结论"),
                    root.path("riskLevel").asText("MEDIUM"),
                    root.path("confidence").asDouble(75),
                    insights,
                    recommendations,
                    nextActions,
                    root.path("source").asText("AI")
            );
        } catch (IOException ex) {
            throw new IllegalStateException("AI 服务返回内容无法解析为 JSON", ex);
        }
    }

            private AiAnalysisDTO buildLocalAnalysis(User user,
                                                     FootprintSummary summary,
                                                     CarbonPredictionDTO prediction,
                                                     List<CarbonPredictionHistoryDTO> history) {
            double currentEmission = safeValue(summary != null ? summary.getTotalEmission() : null);
            double predictedEmission = safeValue(prediction != null ? prediction.getPredictedEmission() : null);
            double delta = predictedEmission - currentEmission;
            double deltaPercent = currentEmission > 0 ? (delta / currentEmission) * 100 : 0;
                    Map<String, Double> categories = new LinkedHashMap<>();
                    categories.put("交通", safeValue(summary != null ? summary.getTransportEmission() : null));
                    categories.put("饮食", safeValue(summary != null ? summary.getDietEmission() : null));
                    categories.put("用电", safeValue(summary != null ? summary.getElectricityEmission() : null));

                    Map.Entry<String, Double> topCategoryEntry = categories.entrySet().stream()
                        .max(Comparator.comparingDouble(Map.Entry::getValue))
                        .orElse(Map.entry("综合排放", 0D));
                    String topCategory = topCategoryEntry.getKey();
                    double topCategoryValue = topCategoryEntry.getValue();
                    double topCategoryShare = currentEmission > 0 ? (topCategoryValue / currentEmission) * 100 : 0;

                    List<CarbonPredictionHistoryDTO> completedHistory = history == null
                        ? List.of()
                        : history.stream()
                            .filter(item -> item.getActualEmission() != null && item.getErrorRate() != null)
                            .sorted(Comparator.comparing(item -> parseYearMonth(item.getTargetMonth())))
                            .collect(Collectors.toList());

                    double averageErrorRate = completedHistory.stream()
                        .mapToDouble(CarbonPredictionHistoryDTO::getErrorRate)
                        .average()
                        .orElse(0D);

                    double forecastConfidence = prediction != null && prediction.getConfidence() != null
                        ? prediction.getConfidence() * 100
                        : 45D;

                    double confidence = forecastConfidence
                        + Math.min(10D, completedHistory.size() * 1.5D)
                        - Math.min(18D, averageErrorRate * 0.4D)
                        + (currentEmission > 0 ? 4D : -6D);
                    confidence = Math.max(35D, Math.min(92D, confidence));

                    List<CarbonPredictionHistoryDTO> recentThree = completedHistory.stream()
                        .skip(Math.max(0, completedHistory.size() - 3L))
                        .collect(Collectors.toList());
                    String recentTrendText = buildRecentTrendText(recentThree);
                    boolean risingStreak = hasConsecutiveIncrease(recentThree);
                    String rankingText = buildRankingText(categories, currentEmission);

            String trendText = Optional.ofNullable(prediction)
                .map(CarbonPredictionDTO::getTrend)
                .filter(text -> !text.isBlank())
                .orElse("暂时无法识别明显趋势");

            String conclusionText = currentEmission <= 0
                ? "本月数据仍偏少，当前结论以趋势观察为主，建议继续补充记录。"
                : String.format("本月总排放 %.2f kg CO₂e，主要由%s贡献，占比约 %.1f%%；与下月预测相比，整体%s。", currentEmission, topCategory, topCategoryShare, delta >= 0 ? "存在上升压力" : "有下降空间");

            String riskText;
            if (risingStreak) {
                riskText = String.format("最近 3 次已完成记录连续上升，且%s是当前最主要排放项，建议重点压降。", topCategory);
            } else if (deltaPercent >= 8 || topCategoryShare >= 50) {
                riskText = String.format("%s占比偏高（%.1f%%），下月排放有上行风险，需要优先处理。", topCategory, topCategoryShare);
            } else if (confidence < 60) {
                riskText = "当前历史记录仍偏少，判断稳定性一般，建议继续积累数据后再做更精细决策。";
            } else {
                riskText = "当前风险整体可控，但仍应持续跟踪交通、饮食和用电中的高占比项。";
            }

            String nextStepText = String.format(
                "1. 优先优化%s；2. 盯住最近 3 次变化节奏；3. 下次刷新时复核预测与实际偏差。",
                topCategory);

            String headline;
                    if (currentEmission <= 0 && predictedEmission <= 0) {
                        headline = "本月运营月报：正在等待更多排放数据";
                    } else if (deltaPercent >= 8 || topCategoryShare >= 50) {
                        headline = "本月运营月报：下月排放存在明显上升压力";
                    } else if (deltaPercent <= -5 && confidence >= 50) {
                        headline = "本月运营月报：下月排放预计继续下降";
            } else {
                headline = "本月运营月报：下月排放整体保持平稳";
            }

                    String deltaText = delta >= 0
                        ? String.format("预计变化为上升 %.2f kg CO₂e（%.1f%%）", delta, deltaPercent)
                        : String.format("预计变化为下降 %.2f kg CO₂e（%.1f%%）", Math.abs(delta), Math.abs(deltaPercent));

                    String summaryText = String.format(
                        "%s的本月运营月报已完成。%s；风险点：%s；下月重点动作：%s",
                        user.getName(),
                        conclusionText,
                        riskText,
                        nextStepText);

            List<AiAnalysisDTO.Insight> insights = List.of(
                new AiAnalysisDTO.Insight(
                    "本月结论",
                    conclusionText),
                new AiAnalysisDTO.Insight(
                    "风险点",
                    riskText),
                new AiAnalysisDTO.Insight(
                    "下月重点动作",
                    nextStepText),
                new AiAnalysisDTO.Insight(
                            "排放占比排序",
                            rankingText),
                new AiAnalysisDTO.Insight(
                            "趋势判断",
                            delta >= 0
                                ? String.format("下月预计比本月增加 %.2f kg CO₂e，建议提前压降高消耗行为。", delta)
                                : String.format("下月预计比本月减少 %.2f kg CO₂e，当前减排习惯已有正向效果。", Math.abs(delta))),
                new AiAnalysisDTO.Insight(
                            "最近 3 次变化",
                            recentTrendText),
                        new AiAnalysisDTO.Insight(
                            "连续上升提醒",
                            risingStreak
                                ? "最近 3 次已完成记录呈连续上升，建议把本月重点放在最主要排放源的压降上。"
                                : "最近 3 次已完成记录没有形成连续上升，当前压力主要来自结构性高排放项。"),
                        new AiAnalysisDTO.Insight(
                            "历史预测质量",
                            completedHistory.isEmpty()
                                ? "当前暂无可用于评估误差的历史记录，建议继续积累月度数据。"
                                : String.format("已有 %d 条已完成预测记录，平均误差率约 %.1f%%，本地分析可信度会随数据继续提升。", completedHistory.size(), averageErrorRate)),
                new AiAnalysisDTO.Insight(
                            "分析可信度",
                            String.format("当前本地规则分析置信度约 %.0f%%，适合用于趋势参考和行动优先级排序。", confidence))
            );

                    List<String> recommendations = new java.util.ArrayList<>();
                    recommendations.add(buildCategoryRecommendation(topCategory));
                    recommendations.add("每周复盘一次交通、饮食和用电记录，重点检查是否有连续两周以上的上升。");
                    recommendations.add("最近 3 次变化节奏为：" + recentTrendText);
                    recommendations.add(prediction != null && prediction.getSuggestion() != null
                        ? prediction.getSuggestion().getSuggestion()
                        : "结合实际记录持续校验预测结果，逐步提高本地分析精度。");

                    if (confidence < 60) {
                        recommendations.add("当前历史数据还不够稳定，建议继续补充记录，提升本地分析的判断质量。");
                    }

                    List<String> nextActions = new java.util.ArrayList<>();
                    nextActions.add("完成本周一次排放复盘并记录变化原因。");
                    nextActions.add("针对 " + topCategory + " 制定一个可执行的减排动作并坚持执行一周。");
                    nextActions.add("把最近 3 次变化整理成一条月报结论，持续跟踪是否出现连续上升。");
                    nextActions.add("下次数据刷新后继续观察预测与实际是否收敛。");

                    if (!completedHistory.isEmpty()) {
                        nextActions.add("对比最近一条已完成预测的误差，检查本地判断是否与实际一致。");
                    }

            return new AiAnalysisDTO(
                        "local-rule-engine",
                LocalDateTime.now(),
                headline,
                summaryText,
                chooseRiskLevel(deltaPercent),
                confidence,
                insights,
                recommendations,
                nextActions,
                        "LOCAL_ANALYSIS"
            );
            }

    private String buildRankingText(Map<String, Double> categories, double totalEmission) {
        return categories.entrySet().stream()
            .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
            .map(entry -> String.format("%s %.2f kg CO₂e（%.1f%%）", entry.getKey(), entry.getValue(), totalEmission > 0 ? entry.getValue() / totalEmission * 100 : 0))
            .collect(Collectors.joining(" > "));
    }

    private String buildRecentTrendText(List<CarbonPredictionHistoryDTO> recentThree) {
        if (recentThree == null || recentThree.isEmpty()) {
            return "暂无历史记录可用于分析";
        }

        List<String> labels = new LinkedList<>();
        for (CarbonPredictionHistoryDTO item : recentThree) {
            labels.add(String.format("%s %.2f kg CO₂e", item.getTargetMonth(), safeValue(item.getActualEmission())));
        }

        if (recentThree.size() == 1) {
            return labels.get(0) + "，历史样本不足，暂时只做单点参考。";
        }

        double first = safeValue(recentThree.get(0).getActualEmission());
        double last = safeValue(recentThree.get(recentThree.size() - 1).getActualEmission());
        double change = first == 0 ? 0 : (last - first) / first * 100;
        String direction = change > 0 ? "上升" : change < 0 ? "下降" : "持平";

        String firstLabel = labels.get(0);
        String lastLabel = labels.get(labels.size() - 1);
        return String.join("，", labels) + String.format("，从%s 到 %s，整体%s %.1f%%。", firstLabel, lastLabel, direction, Math.abs(change));
    }

    private boolean hasConsecutiveIncrease(List<CarbonPredictionHistoryDTO> recentThree) {
        if (recentThree == null || recentThree.size() < 3) {
            return false;
        }

        double first = safeValue(recentThree.get(0).getActualEmission());
        double second = safeValue(recentThree.get(1).getActualEmission());
        double third = safeValue(recentThree.get(2).getActualEmission());
        return first < second && second < third;
    }

    private YearMonth parseYearMonth(String monthText) {
        try {
            return YearMonth.parse(monthText);
        } catch (Exception ex) {
            return YearMonth.of(1970, 1);
        }
    }

                    private String chooseRiskLevel(double deltaPercent) {
                    if (deltaPercent >= 8) {
                return "HIGH";
            }
                    if (deltaPercent <= -5) {
                return "LOW";
            }
            return "MEDIUM";
            }

            private double safeValue(Double value) {
            return value == null ? 0D : value;
            }

            private String buildCategoryRecommendation(String category) {
                if ("交通".equals(category)) {
                    return "优先减少单人出行和高频短途车程，能步行或骑行的场景尽量替代。";
                }
                if ("饮食".equals(category)) {
                    return "优先减少高碳饮食占比，尽量增加植物性食物并减少浪费。";
                }
                if ("用电".equals(category)) {
                    return "优先优化空调、照明和待机耗电，晚间关闭不必要电器。";
                }
                return "优先关注总排放最高的活动类型，先从最容易执行的动作开始。";
            }

    private String normalizeBaseUrl(String value) {
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }

    private String buildEndpoint() {
        String normalizedBaseUrl = normalizeBaseUrl(baseUrl);
        if (isOllamaProvider()) {
            return normalizedBaseUrl + "/api/chat";
        }

        return normalizedBaseUrl + "/chat/completions";
    }

    private boolean isOllamaProvider() {
        return provider != null && provider.equalsIgnoreCase("ollama");
    }

    private boolean isOpenAiCompatibleProvider() {
        return provider != null && (
                provider.equalsIgnoreCase("openai")
                        || provider.equalsIgnoreCase("openai-compatible")
                        || provider.equalsIgnoreCase("deepseek")
                        || provider.equalsIgnoreCase("doubao")
                        || provider.equalsIgnoreCase("zhipu")
        );
    }

    private boolean isOpenRouterProvider() {
        return provider != null && provider.equalsIgnoreCase("openrouter");
    }

    private boolean isLocalProvider() {
        return provider == null || provider.isBlank() || provider.equalsIgnoreCase("local");
    }

    private boolean requiresApiKey() {
        return isOpenAiCompatibleProvider() || isOpenRouterProvider();
    }

    private static final class CachedAnalysis {
        private final AiAnalysisDTO analysis;
        private final LocalDateTime expiresAt;

        private CachedAnalysis(AiAnalysisDTO analysis, LocalDateTime expiresAt) {
            this.analysis = analysis;
            this.expiresAt = expiresAt;
        }
    }
}