第一步：后端构建大模型解析服务 (Java)
我们不要去污染原有的核心业务代码，专门建一个 AiInputParserService.java，把大模型的 HTTP 请求和 JSON 提取封装起来。

在 com.carbonfootprint.service 包下新建服务：

Java
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

    @Value("${zhipu.api.key}") // 在 application.yml 中配置你的 apikey
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 严苛的 System Prompt，逼迫大模型只吐出标准 JSON
    private static final String SYSTEM_PROMPT = 
        "你是一个零碳生活数据提取引擎。请从用户的文字或图片中提取出交通、饮食、用电等碳排放相关信息。\n" +
        "规则：地铁/公交1站按1.5公里计，未说明重量的肉类默认150克。提取结果必须精准映射到以下分类：\n" +
        "transport (type, distance), diet (type, weight), electricity (cost)。\n" +
        "请严格返回 JSON 格式，绝不允许包含任何 markdown 符号、```json 或额外说明文字。示例：\n" +
        "{\"transport\": {\"type\": \"subway\", \"distance\": 15}, \"diet\": {\"type\": \"beef\", \"weight\": 150}}";

    public JsonNode parseInput(String text, String base64Image) {
        String url = "[https://open.bigmodel.cn/api/paas/v4/chat/completions](https://open.bigmodel.cn/api/paas/v4/chat/completions)";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        // 如果传入了图片，使用多模态模型；否则使用速度更快的纯文本模型
        String model = (base64Image != null && !base64Image.isEmpty()) ? "glm-4v-flash" : "glm-4-flash";

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("temperature", 0.1); // 温度调低，保证输出的确定性和 JSON 格式稳定性

        // 构造消息体
        Object content;
        if ("glm-4v-flash".equals(model)) {
            content = new Object[]{
                Map.of("type", "text", "text", text == null ? "提取图片中的碳排放相关数据" : text),
                Map.of("type", "image_url", "image_url", Map.of("url", base64Image))
            };
        } else {
            content = text;
        }

        requestBody.put("messages", new Object[]{
            Map.of("role", "system", "content", SYSTEM_PROMPT),
            Map.of("role", "user", "content", content)
        });

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            JsonNode rootNode = objectMapper.readTree(response.getBody());
            String jsonString = rootNode.path("choices").get(0).path("message").path("content").asText();
            
            // 返回大模型提取并格式化好的 JSON 数据，随后你可以将其送入你的 EmissionStrategyFactory 进行计算
            return objectMapper.readTree(jsonString);
        } catch (Exception e) {
            throw new RuntimeException("AI 解析失败，请重试", e);
        }
    }
}
紧接着，在 AiAnalysisController.java 中暴露接口：

Java
@PostMapping("/chat-to-track")
public ApiResult<JsonNode> chatToTrack(@RequestBody Map<String, String> payload) {
    String text = payload.get("text");
    String imageBase64 = payload.get("image"); // 前端传来的 base64 图片字符串
    
    JsonNode parsedData = aiInputParserService.parseInput(text, imageBase64);
    
    // TODO: 这里调用你已有的 EmissionCalculationStrategy 将 parsedData 转化为具体的碳排放数值并入库
    
    return ApiResult.success(parsedData);
}