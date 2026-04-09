package com.carbonfootprint.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "carbon_prediction_history", uniqueConstraints = {
        @UniqueConstraint(name = "uk_prediction_user_target_month", columnNames = {"user_id", "target_month"})
})
@Data
public class CarbonPredictionHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "target_month", nullable = false, length = 7)
    private String targetMonth;

    @Column(name = "prediction_date", nullable = false)
    private LocalDate predictionDate;

    @Column(name = "predicted_emission", nullable = false)
    private Double predictedEmission;

    @Column(name = "confidence", nullable = false)
    private Double confidence;

    @Column(name = "trend")
    private String trend;

    @Column(name = "actual_emission")
    private Double actualEmission;

    @Column(name = "absolute_error")
    private Double absoluteError;

    @Column(name = "error_rate")
    private Double errorRate;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}