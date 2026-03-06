package com.carbonfootprint.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "points_record")
@Data
public class PointsRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "points_change", nullable = false)
    private Integer pointsChange;
    
    @Column(name = "total_points", nullable = false)
    private Integer totalPoints;
    
    @Column(name = "emission_reduced", nullable = false, columnDefinition = "DOUBLE DEFAULT 0")
    private Double emissionReduced;
    
    @Column(name = "reason", nullable = false)
    private String reason;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
