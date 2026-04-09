package com.carbonfootprint.repository;

import com.carbonfootprint.entity.TransportEmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransportEmissionRepository extends JpaRepository<TransportEmission, Long> {
    List<TransportEmission> findByUserId(Long userId);
    java.util.Optional<TransportEmission> findByIdAndUserId(Long id, Long userId);
    List<TransportEmission> findByUserIdAndEmissionDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
    List<TransportEmission> findByEmissionDate(LocalDate emissionDate);
    void deleteByUserId(Long userId);
}
