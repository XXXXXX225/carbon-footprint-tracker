package com.carbonfootprint.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "footprint_summary")
@Data
public class FootprintSummary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "period", nullable = false)
    private Period period;
    
    @Column(name = "period_start_date", nullable = false)
    private LocalDate periodStartDate;
    
    @Column(name = "period_end_date", nullable = false)
    private LocalDate periodEndDate;
    
    @Column(name = "transport_emission", nullable = false, columnDefinition = "DOUBLE DEFAULT 0")
    private Double transportEmission;
    
    @Column(name = "diet_emission", nullable = false, columnDefinition = "DOUBLE DEFAULT 0")
    private Double dietEmission;
    
    @Column(name = "electricity_emission", nullable = false, columnDefinition = "DOUBLE DEFAULT 0")
    private Double electricityEmission;
    
    @Column(name = "total_emission", nullable = false, columnDefinition = "DOUBLE DEFAULT 0")
    private Double totalEmission;
    
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
    
    public enum Period {
        DAILY, WEEKLY, MONTHLY, YEARLY
    }
}
