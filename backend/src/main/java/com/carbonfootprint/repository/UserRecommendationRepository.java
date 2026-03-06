package com.carbonfootprint.repository;

import com.carbonfootprint.entity.UserRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRecommendationRepository extends JpaRepository<UserRecommendation, Long> {
    List<UserRecommendation> findByUserId(Long userId);
    List<UserRecommendation> findByUserIdAndStatus(Long userId, UserRecommendation.Status status);
    boolean existsByUserIdAndRecommendationId(Long userId, Long recommendationId);
}
