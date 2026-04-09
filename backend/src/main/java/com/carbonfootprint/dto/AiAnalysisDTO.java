package com.carbonfootprint.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiAnalysisDTO {
    private String model;
    private LocalDateTime generatedAt;
    private String headline;
    private String summary;
    private String riskLevel;
    private Double confidence;
    private List<Insight> insights;
    private List<String> recommendations;
    private List<String> nextActions;
    private String source;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Insight {
        private String title;
        private String text;
    }
}