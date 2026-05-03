package com.carbonfootprint.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class AiInputParserService {

    @Value("${ai.api-key}") 
    private String textApiKey;

    @Value("${ai.vision-api-key}") 
    private String imageApiKey;

    @Value("${ai.base-url}")
    private String baseUrl;

    @Value("${ai.model}")
    private String textModel;

    @Value("${ai.vision-model}")
    private String visionModel;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 【核心巨变】提示词重构，让大模型自己去联网算！
    private static final String SYSTEM_PROMPT = 
        "你是一个具备联网搜索能力的碳足迹计算专家。用户会输入任意的活动、饮食或物品。\n" +
        "任务：请你利用联网搜索查明对应的碳排放因子，并直接计算出总碳排放量（千克CO2e）。\n" +
        "要求：不要任何Markdown标记，严格返回JSON数组，格式如下：\n" +
        "[\n" +
        "  {\n" +
        "    \"category\": \"diet\", // 必须是 diet, transport, electricity 之一\n" +
        "    \"itemName\": \"一斤牛肉\", // 用户的原始物品\n" +
        "    \"amount\": 0.5, // 提取出的数值(如0.5kg)\n" +
        "    \"emissionAmount\": 13.5, // 你计算出的碳排放量(kg)\n" +
        "    \"description\": \"联网搜索得知牛肉排放因子约为27kg/kg，计算过程：0.5 * 27 = 13.5kg\"\n" +
        "  }\n" +
        "]";

    public JsonNode parseInput(String text, String base64Image) {
        String url = baseUrl + "/chat/completions";
        boolean isImageRequest = (base64Image != null && !base64Image.isEmpty());
        String model = isImageRequest ? visionModel : textModel;
        String activeApiKey = isImageRequest ? imageApiKey : textApiKey;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + activeApiKey);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("temperature", 0.1); 

        // 【大招开启】启用智谱的 web_search 联网搜索工具
        Map<String, Object> webSearch = new HashMap<>();
        webSearch.put("enable", true);
        Map<String, Object> tool = new HashMap<>();
        tool.put("type", "web_search");
        tool.put("web_search", webSearch);
        requestBody.put("tools", new Object[]{tool});

        Object content = isImageRequest ? new Object[]{
                Map.of("type", "text", "text", text == null ? "计算图片中的碳排放" : text),
                Map.of("type", "image_url", "image_url", Map.of("url", base64Image))
        } : text;

        requestBody.put("messages", new Object[]{
            Map.of("role", "system", "content", SYSTEM_PROMPT),
            Map.of("role", "user", "content", content)
        });

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            JsonNode rootNode = objectMapper.readTree(response.getBody());
            String jsonString = rootNode.path("choices").get(0).path("message").path("content").asText();
            
            // 剔除可能存在的 markdown
            int start = jsonString.indexOf("[");
            int end = jsonString.lastIndexOf("]");
            if (start != -1 && end != -1 && start <= end) {
                jsonString = jsonString.substring(start, end + 1);
            } else {
                start = jsonString.indexOf("{");
                end = jsonString.lastIndexOf("}");
                if (start != -1 && end != -1 && start <= end) {
                    jsonString = "[" + jsonString.substring(start, end + 1) + "]";
                }
            }
            return objectMapper.readTree(jsonString);
        } catch (Exception e) {
            throw new RuntimeException("AI 解析失败: " + e.getMessage(), e);
        }
    }
}