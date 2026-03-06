package com.carbonfootprint.repository;

import com.carbonfootprint.entity.ElectricityEmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ElectricityEmissionRepository extends JpaRepository<ElectricityEmission, Long> {
    List<ElectricityEmission> findByUserId(Long userId);
    List<ElectricityEmission> findByUserIdAndEmissionDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
}
