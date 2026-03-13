package com.carbonfootprint.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "diet_emissions")
@Data
public class DietEmission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "food_type", nullable = false, columnDefinition = "VARCHAR(50)")
    private String foodType;

    @Column(name = "specific_food", nullable = false)
    private String specificFood;

    @Column(name = "amount", nullable = false, columnDefinition = "DECIMAL(10,2)")
    private Double amount;

    @Column(name = "cooking_method")
    private String cookingMethod;

    @Column(name = "emission_amount", nullable = false, columnDefinition = "DECIMAL(10,2)")
    private Double emissionAmount;

    @Column(name = "emission_date", nullable = false)
    private LocalDate emissionDate;

    @Column(name = "description")
    private String description;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
