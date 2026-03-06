package com.carbonfootprint.controller;

import com.carbonfootprint.dto.UserEmissionStats;
import com.carbonfootprint.entity.Recommendation;
import com.carbonfootprint.entity.UserRecommendation;
import com.carbonfootprint.service.PersonalizedRecommendationService;
import com.carbonfootprint.service.RecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/recommendations")
@RequiredArgsConstructor
@Tag(name = "减排建议", description = "减排建议相关接口")
public class RecommendationController {

    private final RecommendationService recommendationService;
    private final PersonalizedRecommendationService personalizedRecommendationService;

    /**
     * 获取减排建议列表
     * 
     * @param authentication 认证信息
     * @param category       建议类别（可选）
     * @return 减排建议列表
     */
    @GetMapping
    @Operation(summary = "获取减排建议", description = "获取减排建议列表，可按类别筛选")
    public ResponseEntity<List<Recommendation>> getRecommendations(
            Authentication authentication,
            @RequestParam(required = false) Recommendation.Category category) {
        List<Recommendation> recommendations;
        if (category != null) {
            recommendations = recommendationService.getRecommendationsByCategory(category);
        } else {
            recommendations = recommendationService.getAllRecommendations();
        }
        return ResponseEntity.ok(recommendations);
    }

    /**
     * 获取用户的个性化减排建议
     * 
     * @param authentication 认证信息
     * @return 个性化减排建议列表
     */
    @GetMapping("/personalized")
    @Operation(summary = "获取个性化减排建议", description = "根据用户碳排放情况生成的个性化减排建议")
    public ResponseEntity<List<Recommendation>> getPersonalizedRecommendations(Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        List<Recommendation> recommendations = recommendationService.generatePersonalizedRecommendations(userId);
        return ResponseEntity.ok(recommendations);
    }

    /**
     * 获取用户已采纳的减排建议
     * 
     * @param authentication 认证信息
     * @param status         建议状态（可选）
     * @return 已采纳的减排建议列表
     */
    @GetMapping("/adopted")
    @Operation(summary = "获取已采纳的减排建议", description = "获取用户已采纳的减排建议，可按状态筛选")
    public ResponseEntity<List<Recommendation>> getAdoptedRecommendations(
            Authentication authentication,
            @RequestParam(required = false) UserRecommendation.Status status) {
        Long userId = getUserIdFromAuthentication(authentication);
        List<Recommendation> recommendations;
        if (status != null) {
            recommendations = recommendationService.getAdoptedRecommendations(userId, status);
        } else {
            // 默认获取所有状态的已采纳建议
            recommendations = recommendationService.getAdoptedRecommendations(userId,
                    UserRecommendation.Status.PENDING);
            recommendations.addAll(
                    recommendationService.getAdoptedRecommendations(userId, UserRecommendation.Status.IN_PROGRESS));
            recommendations.addAll(
                    recommendationService.getAdoptedRecommendations(userId, UserRecommendation.Status.COMPLETED));
        }
        return ResponseEntity.ok(recommendations);
    }

    /**
     * 用户采纳减排建议
     * 
     * @param authentication   认证信息
     * @param recommendationId 建议ID
     * @return 用户建议关联记录
     */
    @PostMapping("/adopt/{recommendationId}")
    @Operation(summary = "采纳减排建议", description = "用户采纳指定的减排建议")
    public ResponseEntity<UserRecommendation> adoptRecommendation(
            Authentication authentication,
            @PathVariable Long recommendationId) {
        Long userId = getUserIdFromAuthentication(authentication);
        UserRecommendation userRecommendation = recommendationService.adoptRecommendation(userId, recommendationId);
        return ResponseEntity.ok(userRecommendation);
    }

    /**
     * 更新用户减排建议状态
     * 
     * @param authentication   认证信息
     * @param recommendationId 建议ID
     * @param status           新状态
     * @return 更新后的用户建议关联记录
     */
    @PutMapping("/update/{recommendationId}")
    @Operation(summary = "更新减排建议状态", description = "更新用户已采纳减排建议的状态")
    public ResponseEntity<UserRecommendation> updateRecommendationStatus(
            Authentication authentication,
            @PathVariable Long recommendationId,
            @RequestParam UserRecommendation.Status status) {
        Long userId = getUserIdFromAuthentication(authentication);
        UserRecommendation userRecommendation = recommendationService.updateRecommendationStatus(userId,
                recommendationId, status);
        return ResponseEntity.ok(userRecommendation);
    }

    /**
     * 从认证信息中获取用户ID
     * 
     * @param authentication 认证信息
     * @return 用户ID
     */
    private Long getUserIdFromAuthentication(Authentication authentication) {
        com.carbonfootprint.entity.User user = (com.carbonfootprint.entity.User) authentication.getPrincipal();
        return user.getId();
    }

    @GetMapping("/personalized/by-category")
    @Operation(summary = "获取按类别分组的个性化建议", description = "根据用户碳排放情况生成的个性化减排建议，按类别分组")
    public ResponseEntity<Map<String, List<Recommendation>>> getPersonalizedRecommendationsByCategory(
            Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        Map<String, List<Recommendation>> recommendations = personalizedRecommendationService
                .getRecommendationsByCategory(userId);
        return ResponseEntity.ok(recommendations);
    }

    @GetMapping("/stats")
    @Operation(summary = "获取用户排放统计数据", description = "获取用户的碳排放统计数据，用于生成个性化建议")
    public ResponseEntity<UserEmissionStats> getUserEmissionStats(Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        UserEmissionStats stats = personalizedRecommendationService.calculateUserEmissionStats(userId);
        return ResponseEntity.ok(stats);
    }
}
