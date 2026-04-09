package com.carbonfootprint.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarbonPredictionHistoryDTO {
    private Long id;
    private String targetMonth;
    private LocalDate predictionDate;
    private Double predictedEmission;
    private Double confidence;
    private String trend;
    private Double actualEmission;
    private Double absoluteError;
    private Double errorRate;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}