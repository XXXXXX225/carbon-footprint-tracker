package com.carbonfootprint.repository;

import com.carbonfootprint.entity.FootprintSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FootprintSummaryRepository extends JpaRepository<FootprintSummary, Long> {
    List<FootprintSummary> findByUserId(Long userId);

    Optional<FootprintSummary> findByUserIdAndPeriodAndPeriodStartDateAndPeriodEndDate(
            Long userId,
            FootprintSummary.Period period,
            LocalDate periodStartDate,
            LocalDate periodEndDate);

    List<FootprintSummary> findByUserIdAndPeriodOrderByPeriodStartDateDesc(Long userId, FootprintSummary.Period period);

    List<FootprintSummary> findByUserIdAndPeriod(Long userId, String period);

    List<FootprintSummary> findByUserIdAndPeriodStartDateAfter(Long userId, LocalDate periodStartDate);
}
