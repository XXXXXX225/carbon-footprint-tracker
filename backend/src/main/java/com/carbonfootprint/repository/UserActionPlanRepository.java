package com.carbonfootprint.repository;

import com.carbonfootprint.entity.UserActionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserActionPlanRepository extends JpaRepository<UserActionPlan, Long> {
    List<UserActionPlan> findByUserId(Long userId);
    List<UserActionPlan> findByUserIdAndStatus(Long userId, UserActionPlan.Status status);
    boolean existsByUserIdAndActionPlanId(Long userId, Long actionPlanId);
}