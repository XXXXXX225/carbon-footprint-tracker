package com.carbonfootprint.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TransportEmissionDTO {
    private Integer transportType;
    private Double distance;
    private String fuelType;
    private Double fuelConsumption;
    private LocalDate emissionDate;
    private String description;
}
