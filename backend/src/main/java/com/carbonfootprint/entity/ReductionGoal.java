package com.carbonfootprint.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "reduction_goals")
@Data
public class ReductionGoal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "target_percentage", nullable = false)
    private Double targetPercentage;
    
    @Column(name = "baseline_emission", nullable = false)
    private Double baselineEmission;
    
    @Column(name = "target_emission", nullable = false)
    private Double targetEmission;
    
    @Column(name = "current_emission")
    private Double currentEmission;
    
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;
    
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private GoalStatus status;
    
    @Column(name = "progress", nullable = false)
    private Double progress;
    
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
    
    public enum GoalStatus {
        ACTIVE,
        COMPLETED,
        FAILED,
        CANCELLED
    }
}