package com.carbonfootprint.service;

import com.carbonfootprint.entity.ReductionGoal;
import com.carbonfootprint.repository.FootprintSummaryRepository;
import com.carbonfootprint.repository.ReductionGoalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GoalTrackingService {

    private final ReductionGoalRepository goalRepository;
    private final FootprintSummaryRepository summaryRepository;

    public ReductionGoal createGoal(Long userId, Double targetPercentage, LocalDate endDate) {
        double baselineEmission = calculateBaselineEmission(userId);
        double targetEmission = baselineEmission * (1 - targetPercentage / 100);

        ReductionGoal goal = new ReductionGoal();
        goal.setUserId(userId);
        goal.setTargetPercentage(targetPercentage);
        goal.setBaselineEmission(baselineEmission);
        goal.setTargetEmission(targetEmission);
        goal.setCurrentEmission(0.0);
        goal.setStartDate(LocalDate.now());
        goal.setEndDate(endDate);
        goal.setStatus(ReductionGoal.GoalStatus.ACTIVE);
        goal.setProgress(0.0);

        return goalRepository.save(goal);
    }

    public void updateGoalProgress(Long goalId) {
        ReductionGoal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("目标不存在"));

        double currentEmission = calculateCurrentPeriodEmission(goal.getUserId(), goal.getStartDate());
        goal.setCurrentEmission(currentEmission);

        double progress = calculateProgress(goal);
        goal.setProgress(progress);

        if (progress >= 100) {
            goal.setStatus(ReductionGoal.GoalStatus.COMPLETED);
        } else if (LocalDate.now().isAfter(goal.getEndDate())) {
            goal.setStatus(ReductionGoal.GoalStatus.FAILED);
        }

        goalRepository.save(goal);
    }

    public List<ReductionGoal> getUserGoals(Long userId) {
        return goalRepository.findByUserId(userId);
    }

    public ReductionGoal getActiveGoal(Long userId) {
        return goalRepository.findActiveGoalByUserId(userId).orElse(null);
    }

    public void cancelGoal(Long goalId) {
        ReductionGoal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("目标不存在"));
        goal.setStatus(ReductionGoal.GoalStatus.CANCELLED);
        goalRepository.save(goal);
    }

    private double calculateBaselineEmission(Long userId) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(1);

        return summaryRepository.findByUserIdAndPeriod(userId, "MONTH")
                .stream()
                .mapToDouble(summary -> summary.getTotalEmission())
                .findFirst()
                .orElse(100.0);
    }

    private double calculateCurrentPeriodEmission(Long userId, LocalDate startDate) {
        return summaryRepository.findByUserIdAndPeriodStartDateAfter(userId, startDate)
                .stream()
                .mapToDouble(summary -> summary.getTotalEmission())
                .sum();
    }

    private double calculateProgress(ReductionGoal goal) {
        double expectedEmission = calculateExpectedEmission(goal);
        double reduction = goal.getBaselineEmission() - goal.getCurrentEmission();
        double targetReduction = goal.getBaselineEmission() - goal.getTargetEmission();

        return (reduction / targetReduction) * 100;
    }

    private double calculateExpectedEmission(ReductionGoal goal) {
        long daysPassed = ChronoUnit.DAYS.between(goal.getStartDate(), LocalDate.now());
        long totalDays = ChronoUnit.DAYS.between(goal.getStartDate(), goal.getEndDate());

        return goal.getBaselineEmission() * (daysPassed / (double) totalDays);
    }
}