package com.carbonfootprint.repository;

import com.carbonfootprint.entity.CarbonPredictionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarbonPredictionHistoryRepository extends JpaRepository<CarbonPredictionHistory, Long> {
    List<CarbonPredictionHistory> findByUserIdOrderByTargetMonthDesc(Long userId);

    Optional<CarbonPredictionHistory> findByUserIdAndTargetMonth(Long userId, String targetMonth);

    long countByUserId(Long userId);
}