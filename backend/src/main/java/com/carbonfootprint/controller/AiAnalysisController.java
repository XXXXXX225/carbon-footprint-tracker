package com.carbonfootprint.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.carbonfootprint.dto.AiAnalysisDTO;
import com.carbonfootprint.dto.DietEmissionDTO;
import com.carbonfootprint.dto.ElectricityEmissionDTO;
import com.carbonfootprint.dto.TransportEmissionDTO;
import com.carbonfootprint.service.AiInputParserService;
import com.carbonfootprint.dto.ApiResult;
import com.carbonfootprint.entity.User;
import com.carbonfootprint.service.CarbonAiAnalysisService;
import com.carbonfootprint.service.EmissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Tag(name = "AI分析", description = "基于真实模型并支持本地兜底的碳足迹分析接口")
public class AiAnalysisController {

    @Autowired
    private AiInputParserService aiInputParserService;
    @Autowired
    private EmissionService emissionService;
    @Autowired
    private ObjectMapper objectMapper;
    private final CarbonAiAnalysisService aiAnalysisService;

    @GetMapping("/analysis")
    @Operation(summary = "获取AI分析", description = "根据用户碳足迹数据生成分析结论，外部模型不可用时自动回退到本地规则分析")
    public ResponseEntity<ApiResult<AiAnalysisDTO>> getAnalysis(@AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(403).body(ApiResult.error(403, "用户未认证"));
        }

        AiAnalysisDTO analysis = aiAnalysisService.analyze(user.getId());
        return ResponseEntity.ok(ApiResult.success(analysis, "获取成功"));
    }
    @PostMapping("/chat-to-track")
    public ApiResult<JsonNode> chatToTrack(
            @RequestBody Map<String, String> payload,
            @AuthenticationPrincipal User user) {

        if (user == null) {
            return ApiResult.error(403, "用户未登录，无法记录足迹");
        }

        String text = payload.get("text");
        String imageBase64 = payload.get("image");

        JsonNode parsedArray = aiInputParserService.parseInput(text, imageBase64);

        System.out.println("====== AI 动态联网计算结果 ======");
        System.out.println(parsedArray.toPrettyString());
        System.out.println("===============================");

        try {
            Long userId = user.getId();
            int savedCount = 0;

            if (parsedArray.isArray()) {
                for (JsonNode record : parsedArray) {
                    String category = record.path("category").asText("");
                    String itemName = record.path("itemName").asText("未知项目");
                    double amount = record.path("amount").asDouble(1.0);
                    double emissionAmount = record.path("emissionAmount").asDouble(0.0);
                    String desc = record.path("description").asText("AI 自动测算");

                    if (emissionAmount > 0) {
                        if ("diet".equalsIgnoreCase(category)) {
                            emissionService.recordAiDietEmission(userId, itemName, amount, emissionAmount, desc);
                            savedCount++;
                        } else if ("transport".equalsIgnoreCase(category)) {
                            emissionService.recordAiTransportEmission(userId, itemName, amount, emissionAmount, desc);
                            savedCount++;
                        } else if ("electricity".equalsIgnoreCase(category)) {
                            emissionService.recordAiElectricityEmission(userId, itemName, amount, emissionAmount, desc);
                            savedCount++;
                        }
                    }
                }
            }

            if (savedCount == 0) {
                return ApiResult.error(500, "AI 未能计算出有效的碳排放数值，可能是暂未查到该物品的碳排放因子。");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ApiResult.error(500, "数据保存失败：" + e.getMessage());
        }

        return ApiResult.success(parsedArray);
    }
}