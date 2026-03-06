package com.carbonfootprint.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "electricity_emissions")
@Data
public class ElectricityEmission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "device_type", nullable = false)
    private String deviceType;
    
    @Column(name = "power", nullable = false)
    private Double power;
    
    @Column(name = "usage_time", nullable = false)
    private Double usageTime;
    
    @Column(name = "usage_days", nullable = false)
    private Integer usageDays;
    
    @Column(name = "electricity_amount", nullable = false)
    private Double electricityAmount;
    
    @Column(name = "emission_amount", nullable = false)
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
