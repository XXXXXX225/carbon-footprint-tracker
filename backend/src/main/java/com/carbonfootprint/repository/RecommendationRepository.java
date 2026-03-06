package com.carbonfootprint.repository;

import com.carbonfootprint.entity.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {
    List<Recommendation> findByCategory(Recommendation.Category category);
    List<Recommendation> findByDifficulty(Recommendation.Difficulty difficulty);
    List<Recommendation> findByCost(Recommendation.Cost cost);
    List<Recommendation> findByCategoryAndDifficulty(Recommendation.Category category, Recommendation.Difficulty difficulty);
    List<Recommendation> findByCategoryAndCost(Recommendation.Category category, Recommendation.Cost cost);
}
