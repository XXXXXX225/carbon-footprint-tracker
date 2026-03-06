package com.carbonfootprint.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class DietEmissionDTO {
    private Integer foodType;
    private String specificFood;
    private Double amount;
    private String cookingMethod;
    private LocalDate emissionDate;
    private String description;
}
