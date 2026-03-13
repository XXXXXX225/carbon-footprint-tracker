package com.carbonfootprint.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "transport_emissions")
@Data
public class TransportEmission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "transport_type", nullable = false, columnDefinition = "VARCHAR(50)")
    private String transportType;

    @Column(name = "distance", nullable = false, columnDefinition = "DECIMAL(10,2)")
    private Double distance;

    @Column(name = "fuel_type")
    private String fuelType;

    @Column(name = "fuel_consumption", columnDefinition = "DECIMAL(10,2)")
    private Double fuelConsumption;

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
