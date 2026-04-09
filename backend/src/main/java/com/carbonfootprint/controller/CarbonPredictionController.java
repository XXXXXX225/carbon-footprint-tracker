package com.carbonfootprint.controller;

import com.carbonfootprint.dto.ApiResult;
import com.carbonfootprint.dto.CarbonPredictionHistoryDTO;
import com.carbonfootprint.dto.CarbonPredictionDTO;
import com.carbonfootprint.entity.User;
import com.carbonfootprint.service.CarbonPredictionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/prediction")
@RequiredArgsConstructor
@Tag(name = "AI预测", description = "碳排放AI预测接口")
public class CarbonPredictionController {

    private final CarbonPredictionService predictionService;

    @GetMapping("/next-month")
    @Operation(summary = "预测下月碳排放", description = "基于历史数据预测用户下个月的碳排放量")
    public ResponseEntity<ApiResult<CarbonPredictionDTO>> predictNextMonth(
            @AuthenticationPrincipal User user) {
        
        System.out.println("[Prediction Controller] 收到预测请求");
        System.out.println("[Prediction Controller] User: " + user);
        
        if (user == null) {
            System.out.println("[Prediction Controller] User为null，用户未认证");
            return ResponseEntity.status(403).body(ApiResult.error(403, "用户未认证"));
        }
        
        Long userId = user.getId();
        System.out.println("[Prediction Controller] 用户ID: " + userId);
        
        CarbonPredictionDTO prediction = predictionService.predictNextMonth(userId);
        
        return ResponseEntity.ok(ApiResult.success(prediction, "预测成功"));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "预测指定用户下月碳排放", description = "管理员预测指定用户下个月的碳排放量")
    public ResponseEntity<ApiResult<CarbonPredictionDTO>> predictUserNextMonth(
            @PathVariable Long userId) {
        
        CarbonPredictionDTO prediction = predictionService.predictNextMonth(userId);
        
        return ResponseEntity.ok(ApiResult.success(prediction, "预测成功"));
    }

    @GetMapping("/history")
    @Operation(summary = "获取预测历史", description = "获取用户的预测记录以及实际对比数据")
    public ResponseEntity<ApiResult<java.util.List<CarbonPredictionHistoryDTO>>> getPredictionHistory(
            @AuthenticationPrincipal User user) {

        if (user == null) {
            return ResponseEntity.status(403).body(ApiResult.error(403, "用户未认证"));
        }

        return ResponseEntity.ok(ApiResult.success(
                predictionService.getPredictionHistory(user.getId()),
                "获取成功"));
    }
}
