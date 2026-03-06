package com.carbonfootprint.repository;

import com.carbonfootprint.entity.DietEmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DietEmissionRepository extends JpaRepository<DietEmission, Long> {
    List<DietEmission> findByUserId(Long userId);
    List<DietEmission> findByUserIdAndEmissionDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
}
