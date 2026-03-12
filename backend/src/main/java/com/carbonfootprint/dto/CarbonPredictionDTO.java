package com.carbonfootprint.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarbonPredictionDTO {
    private Long userId;
    private LocalDate predictionDate;
    private Double predictedEmission;
    private Double confidence;
    private String trend;
    private List<DailyPrediction> dailyPredictions;
    private List<MonthlyPrediction> monthlyPredictions;
    private ReductionSuggestion suggestion;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyPrediction {
        private LocalDate date;
        private Double predictedEmission;
        private Double lowerBound;
        private Double upperBound;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyPrediction {
        private String month;
        private Double predictedEmission;
        private Double trend;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReductionSuggestion {
        private String category;
        private String suggestion;
        private Double potentialReduction;
        private Integer priority;
    }
}
