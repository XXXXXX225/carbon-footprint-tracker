package com.carbonfootprint.controller;

import com.carbonfootprint.dto.ApiResult;
import com.carbonfootprint.entity.ReductionGoal;
import com.carbonfootprint.service.GoalTrackingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/goals")
@RequiredArgsConstructor
@Tag(name = "目标管理", description = "减排目标管理相关接口")
public class GoalController {
    
    private final GoalTrackingService goalTrackingService;
    
    @PostMapping
    @Operation(summary = "创建减排目标", description = "创建新的减排目标")
    public ApiResult<ReductionGoal> createGoal(
            Authentication authentication,
            @RequestParam Double targetPercentage,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        Long userId = getUserIdFromAuthentication(authentication);
        ReductionGoal goal = goalTrackingService.createGoal(userId, targetPercentage, endDate);
        return ApiResult.success(goal);
    }
    
    @PutMapping("/{goalId}/progress")
    @Operation(summary = "更新目标进度", description = "更新减排目标的进度")
    public ApiResult<Void> updateProgress(
            @PathVariable Long goalId) {
        goalTrackingService.updateGoalProgress(goalId);
        return ApiResult.success(null);
    }
    
    @GetMapping
    @Operation(summary = "获取用户目标列表", description = "获取用户的所有减排目标")
    public ApiResult<List<ReductionGoal>> getUserGoals(Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        List<ReductionGoal> goals = goalTrackingService.getUserGoals(userId);
        return ApiResult.success(goals);
    }
    
    @GetMapping("/active")
    @Operation(summary = "获取活跃目标", description = "获取用户当前活跃的减排目标")
    public ApiResult<ReductionGoal> getActiveGoal(Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        ReductionGoal goal = goalTrackingService.getActiveGoal(userId);
        return ApiResult.success(goal);
    }
    
    @DeleteMapping("/{goalId}")
    @Operation(summary = "取消目标", description = "取消指定的减排目标")
    public ApiResult<Void> cancelGoal(
            @PathVariable Long goalId) {
        goalTrackingService.cancelGoal(goalId);
        return ApiResult.success(null);
    }
    
    private Long getUserIdFromAuthentication(Authentication authentication) {
        com.carbonfootprint.entity.User user = (com.carbonfootprint.entity.User) authentication.getPrincipal();
        return user.getId();
    }
}