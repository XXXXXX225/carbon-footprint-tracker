package com.carbonfootprint.controller;

import com.carbonfootprint.dto.AiAnalysisDTO;
import com.carbonfootprint.dto.ApiResult;
import com.carbonfootprint.entity.User;
import com.carbonfootprint.service.CarbonAiAnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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
}