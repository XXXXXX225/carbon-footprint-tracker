package com.carbonfootprint.controller;

import com.carbonfootprint.dto.UserEmissionStats;
import com.carbonfootprint.entity.ActionPlanItem;
import com.carbonfootprint.entity.UserActionPlan;
import com.carbonfootprint.service.ActionPlanGenerationService;
import com.carbonfootprint.service.ActionPlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/action-plans")
@RequiredArgsConstructor
@Tag(name = "碳行动计划", description = "碳行动计划相关接口")
public class ActionPlanController {

    private final ActionPlanService actionPlanService;
    private final ActionPlanGenerationService actionPlanGenerationService;

    @GetMapping
    @Operation(summary = "获取碳行动计划", description = "获取碳行动计划列表，可按类别筛选")
    public ResponseEntity<List<ActionPlanItem>> getActionPlans(
            Authentication authentication,
            @RequestParam(required = false) ActionPlanItem.Category category) {
        List<ActionPlanItem> actionPlans;
        if (category != null) {
            actionPlans = actionPlanService.getActionPlansByCategory(category);
        } else {
            actionPlans = actionPlanService.getAllActionPlans();
        }
        return ResponseEntity.ok(actionPlans);
    }

    @GetMapping("/personalized")
    @Operation(summary = "获取个性化碳行动计划", description = "根据用户碳排放情况生成的个性化行动计划")
    public ResponseEntity<List<ActionPlanItem>> getPersonalizedActionPlans(Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        List<ActionPlanItem> actionPlans = actionPlanService.generatePersonalizedActionPlans(userId);
        return ResponseEntity.ok(actionPlans);
    }

    @GetMapping("/adopted")
    @Operation(summary = "获取已采纳的碳行动计划", description = "获取用户已采纳的碳行动计划，可按状态筛选")
    public ResponseEntity<List<ActionPlanItem>> getAdoptedActionPlans(
            Authentication authentication,
            @RequestParam(required = false) UserActionPlan.Status status) {
        Long userId = getUserIdFromAuthentication(authentication);
        List<ActionPlanItem> actionPlans;
        if (status != null) {
            actionPlans = actionPlanService.getAdoptedActionPlans(userId, status);
        } else {
            actionPlans = actionPlanService.getAdoptedActionPlans(userId, UserActionPlan.Status.PENDING);
            actionPlans.addAll(actionPlanService.getAdoptedActionPlans(userId, UserActionPlan.Status.IN_PROGRESS));
            actionPlans.addAll(actionPlanService.getAdoptedActionPlans(userId, UserActionPlan.Status.COMPLETED));
        }
        return ResponseEntity.ok(actionPlans);
    }

    @PostMapping("/adopt/{actionPlanId}")
    @Operation(summary = "采纳碳行动计划", description = "用户采纳指定的碳行动计划")
    public ResponseEntity<UserActionPlan> adoptActionPlan(
            Authentication authentication,
            @PathVariable Long actionPlanId) {
        Long userId = getUserIdFromAuthentication(authentication);
        UserActionPlan userActionPlan = actionPlanService.adoptActionPlan(userId, actionPlanId);
        return ResponseEntity.ok(userActionPlan);
    }

    @PutMapping("/update/{actionPlanId}")
    @Operation(summary = "更新碳行动计划状态", description = "更新用户已采纳碳行动计划的状态")
    public ResponseEntity<UserActionPlan> updateActionPlanStatus(
            Authentication authentication,
            @PathVariable Long actionPlanId,
            @RequestParam UserActionPlan.Status status) {
        Long userId = getUserIdFromAuthentication(authentication);
        UserActionPlan userActionPlan = actionPlanService.updateActionPlanStatus(userId, actionPlanId, status);
        return ResponseEntity.ok(userActionPlan);
    }

    @GetMapping("/personalized/by-category")
    @Operation(summary = "获取按类别分组的个性化碳行动计划", description = "根据用户碳排放情况生成的个性化行动计划，按类别分组")
    public ResponseEntity<Map<String, List<ActionPlanItem>>> getPersonalizedActionPlansByCategory(
            Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        Map<String, List<ActionPlanItem>> actionPlans = actionPlanGenerationService.getActionPlansByCategory(userId);
        return ResponseEntity.ok(actionPlans);
    }

    @GetMapping("/stats")
    @Operation(summary = "获取用户排放统计数据", description = "获取用户的碳排放统计数据，用于生成个性化行动计划")
    public ResponseEntity<UserEmissionStats> getUserEmissionStats(Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        UserEmissionStats stats = actionPlanGenerationService.calculateUserEmissionStats(userId);
        return ResponseEntity.ok(stats);
    }

    private Long getUserIdFromAuthentication(Authentication authentication) {
        com.carbonfootprint.entity.User user = (com.carbonfootprint.entity.User) authentication.getPrincipal();
        return user.getId();
    }
}