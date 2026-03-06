package com.carbonfootprint.repository;

import com.carbonfootprint.entity.PointsRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PointsRuleRepository extends JpaRepository<PointsRule, Long> {
    Optional<PointsRule> findByIsActiveTrue();
}
