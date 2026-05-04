package com.carbonfootprint.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.carbonfootprint.dto.ApiResult;
import com.carbonfootprint.entity.User;
import com.carbonfootprint.entity.ActionPlanItem;
import com.carbonfootprint.service.ActionPlanService;
import com.carbonfootprint.service.PointsService;
import com.carbonfootprint.repository.ActionPlanRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Map;

@RestController
@RequestMapping("/api/recommendations")
@Tag(name = "AI 建言", description = "AI 挑战自动认领与积分结算接口")
public class RecommendationController {

    @Autowired
    private ActionPlanRepository actionPlanRepository;

    @Autowired
    private ActionPlanService actionPlanService;
    
    @Autowired
    private PointsService pointsService;

    @PostMapping("/add-from-ai")
    @Operation(summary = "领取 AI 任务", description = "领取由 AI 分析模型生成的减碳挑战")
    public ApiResult<String> addFromAi(
            @RequestBody Map<String, String> payload,
            @AuthenticationPrincipal User user) {
        
        if (user == null) {
            return ApiResult.error(403, "用户未登录");
        }
        
        String content = payload.get("content");
        if (content == null || content.trim().isEmpty()) {
            return ApiResult.error(400, "建议内容不能为空");
        }
        
        // 1. 生成并保存全新的 AI 挑战计划到库中
        ActionPlanItem plan = new ActionPlanItem();
        String titleStr = content;
        if (titleStr.length() > 15) {
            titleStr = titleStr.substring(0, 15) + "...";
        }
        plan.setTitle("AI专属挑战：" + titleStr);
        plan.setDescription("来自 AI 模型的专项挑战任务：\n" + content);
        plan.setCategory(ActionPlanItem.Category.OTHER);
        plan.setDifficulty(ActionPlanItem.Difficulty.MEDIUM);
        plan.setCost(ActionPlanItem.Cost.LOW);
        plan.setImpact(0.5); // 预期减碳量 (kg)
        
        actionPlanRepository.save(plan);
        
        // 2. 当前用户自动认领该计划
        actionPlanService.adoptActionPlan(user.getId(), plan.getId());
        
        // 3. 对接减碳积分：为拥抱 AI 挑战用户发放专属激励积分
        // 规则默认为：(emissionReduced) 乘以 (pointsPerKg). 这里假定折合减排 1.0kg 作为激励基数
        pointsService.calculateAndAwardPoints(user.getId(), 1.0, "领取 AI 环保专属挑战");

        return ApiResult.success("AI 挑战领取成功", "操作成功");
    }
}
