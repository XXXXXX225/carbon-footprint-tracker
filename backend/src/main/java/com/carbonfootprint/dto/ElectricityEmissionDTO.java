package com.carbonfootprint.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ElectricityEmissionDTO {
    private String deviceType;
    private Double power;
    private Double usageTime;
    private Integer usageDays;
    private LocalDate emissionDate;
    private String description;
}
