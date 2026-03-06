package com.carbonfootprint.dto;

import lombok.Data;

@Data
public class EmissionsSummaryDTO {
    
    private Long userId;
    private String period;
    private Double totalEmission;
    private Double transportEmission;
    private Double dietEmission;
    private Double electricityEmission;
    private Integer recordCount;
    private Double averageDailyEmission;
}
