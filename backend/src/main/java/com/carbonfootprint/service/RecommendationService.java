package com.carbonfootprint.service;

import com.carbonfootprint.entity.Recommendation;
import com.carbonfootprint.entity.UserRecommendation;
import com.carbonfootprint.repository.RecommendationRepository;
import com.carbonfootprint.repository.UserRecommendationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final UserRecommendationRepository userRecommendationRepository;
    private final PersonalizedRecommendationService personalizedRecommendationService;

    /**
     * 获取所有减排建议
     * 
     * @return 减排建议列表
     */
    public List<Recommendation> getAllRecommendations() {
        return recommendationRepository.findAll();
    }

    /**
     * 根据类别获取减排建议
     * 
     * @param category 建议类别
     * @return 减排建议列表
     */
    public List<Recommendation> getRecommendationsByCategory(Recommendation.Category category) {
        return recommendationRepository.findByCategory(category);
    }

    /**
     * 获取用户的减排建议
     * 
     * @param userId 用户ID
     * @return 减排建议列表
     */
    public List<Recommendation> getUserRecommendations(Long userId) {
        // 获取用户已采纳的建议ID列表
        List<Long> adoptedRecommendationIds = userRecommendationRepository.findByUserId(userId)
                .stream()
                .map(UserRecommendation::getRecommendationId)
                .collect(Collectors.toList());

        // 获取所有建议，排除已采纳的建议
        List<Recommendation> allRecommendations = recommendationRepository.findAll();
        return allRecommendations.stream()
                .filter(rec -> !adoptedRecommendationIds.contains(rec.getId()))
                .collect(Collectors.toList());
    }

    /**
     * 获取用户已采纳的减排建议
     * 
     * @param userId 用户ID
     * @param status 建议状态
     * @return 减排建议列表
     */
    public List<Recommendation> getAdoptedRecommendations(Long userId, UserRecommendation.Status status) {
        // 获取用户指定状态的建议关联记录
        List<UserRecommendation> userRecommendations = userRecommendationRepository.findByUserIdAndStatus(userId,
                status);

        // 根据建议ID获取建议详情
        List<Long> recommendationIds = userRecommendations.stream()
                .map(UserRecommendation::getRecommendationId)
                .collect(Collectors.toList());

        return recommendationIds.stream()
                .map(id -> recommendationRepository.findById(id).orElse(null))
                .filter(rec -> rec != null)
                .collect(Collectors.toList());
    }

    /**
     * 用户采纳减排建议
     * 
     * @param userId           用户ID
     * @param recommendationId 建议ID
     * @return 用户建议关联记录
     */
    public UserRecommendation adoptRecommendation(Long userId, Long recommendationId) {
        // 检查建议是否存在
        recommendationRepository.findById(recommendationId)
                .orElseThrow(() -> new IllegalArgumentException("减排建议不存在"));

        // 检查用户是否已采纳该建议
        if (userRecommendationRepository.existsByUserIdAndRecommendationId(userId, recommendationId)) {
            throw new IllegalArgumentException("您已采纳该减排建议");
        }

        // 创建用户建议关联记录
        UserRecommendation userRecommendation = new UserRecommendation();
        userRecommendation.setUserId(userId);
        userRecommendation.setRecommendationId(recommendationId);
        userRecommendation.setStatus(UserRecommendation.Status.PENDING);

        return userRecommendationRepository.save(userRecommendation);
    }

    /**
     * 更新用户减排建议状态
     * 
     * @param userId           用户ID
     * @param recommendationId 建议ID
     * @param status           新状态
     * @return 更新后的用户建议关联记录
     */
    public UserRecommendation updateRecommendationStatus(Long userId, Long recommendationId,
            UserRecommendation.Status status) {
        // 查找用户建议关联记录
        List<UserRecommendation> userRecommendations = userRecommendationRepository.findByUserId(userId);
        UserRecommendation userRecommendation = userRecommendations.stream()
                .filter(ur -> ur.getRecommendationId().equals(recommendationId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("您未采纳该减排建议"));

        // 更新状态
        userRecommendation.setStatus(status);
        if (status == UserRecommendation.Status.COMPLETED) {
            userRecommendation.setCompletedAt(LocalDateTime.now());
        }

        return userRecommendationRepository.save(userRecommendation);
    }

    /**
     * 生成个性化减排建议
     * 
     * @param userId 用户ID
     * @return 个性化减排建议列表
     */
    public List<Recommendation> generatePersonalizedRecommendations(Long userId) {
        return personalizedRecommendationService.generatePersonalizedRecommendations(userId);
    }
}
