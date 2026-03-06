package com.carbonfootprint.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_recommendations")
@Data
public class UserRecommendation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "recommendation_id", nullable = false)
    private Long recommendationId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "ENUM('PENDING', 'IN_PROGRESS', 'COMPLETED') DEFAULT 'PENDING'")
    private Status status;
    
    @Column(name = "adopted_at", updatable = false)
    private LocalDateTime adoptedAt;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    @PrePersist
    protected void onCreate() {
        adoptedAt = LocalDateTime.now();
    }
    
    public enum Status {
        PENDING, IN_PROGRESS, COMPLETED
    }
}
