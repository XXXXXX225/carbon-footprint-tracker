package com.carbonfootprint.repository;

import com.carbonfootprint.entity.ActionPlanItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActionPlanRepository extends JpaRepository<ActionPlanItem, Long> {
    List<ActionPlanItem> findByCategory(ActionPlanItem.Category category);
    List<ActionPlanItem> findByDifficulty(ActionPlanItem.Difficulty difficulty);
    List<ActionPlanItem> findByCost(ActionPlanItem.Cost cost);
    List<ActionPlanItem> findByCategoryAndDifficulty(ActionPlanItem.Category category, ActionPlanItem.Difficulty difficulty);
    List<ActionPlanItem> findByCategoryAndCost(ActionPlanItem.Category category, ActionPlanItem.Cost cost);
}