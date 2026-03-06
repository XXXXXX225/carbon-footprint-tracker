package com.carbonfootprint.repository;

import com.carbonfootprint.entity.ReductionGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReductionGoalRepository extends JpaRepository<ReductionGoal, Long> {
    List<ReductionGoal> findByUserId(Long userId);
    Optional<ReductionGoal> findActiveGoalByUserId(Long userId);
}