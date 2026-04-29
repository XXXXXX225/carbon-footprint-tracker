package com.carbonfootprint.service;

import com.carbonfootprint.entity.ActionPlanItem;
import com.carbonfootprint.entity.UserActionPlan;
import com.carbonfootprint.repository.ActionPlanRepository;
import com.carbonfootprint.repository.UserActionPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActionPlanService {

    private final ActionPlanRepository actionPlanRepository;
    private final UserActionPlanRepository userActionPlanRepository;
    private final ActionPlanGenerationService actionPlanGenerationService;

    public List<ActionPlanItem> getAllActionPlans() {
        return actionPlanRepository.findAll();
    }

    public List<ActionPlanItem> getActionPlansByCategory(ActionPlanItem.Category category) {
        return actionPlanRepository.findByCategory(category);
    }

    public List<ActionPlanItem> getUserActionPlans(Long userId) {
        List<Long> adoptedActionPlanIds = userActionPlanRepository.findByUserId(userId)
                .stream()
                .map(UserActionPlan::getActionPlanId)
                .collect(Collectors.toList());

        List<ActionPlanItem> allActionPlans = actionPlanRepository.findAll();
        return allActionPlans.stream()
                .filter(plan -> !adoptedActionPlanIds.contains(plan.getId()))
                .collect(Collectors.toList());
    }

    public List<ActionPlanItem> getAdoptedActionPlans(Long userId, UserActionPlan.Status status) {
        List<UserActionPlan> userActionPlans = userActionPlanRepository.findByUserIdAndStatus(userId, status);
        List<Long> actionPlanIds = userActionPlans.stream()
                .map(UserActionPlan::getActionPlanId)
                .collect(Collectors.toList());

        return actionPlanIds.stream()
                .map(id -> actionPlanRepository.findById(id).orElse(null))
                .filter(plan -> plan != null)
                .collect(Collectors.toList());
    }

    public UserActionPlan adoptActionPlan(Long userId, Long actionPlanId) {
        actionPlanRepository.findById(actionPlanId)
                .orElseThrow(() -> new IllegalArgumentException("行动计划不存在"));

        if (userActionPlanRepository.existsByUserIdAndActionPlanId(userId, actionPlanId)) {
            throw new IllegalArgumentException("您已采纳该行动计划");
        }

        UserActionPlan userActionPlan = new UserActionPlan();
        userActionPlan.setUserId(userId);
        userActionPlan.setActionPlanId(actionPlanId);
        userActionPlan.setStatus(UserActionPlan.Status.PENDING);

        return userActionPlanRepository.save(userActionPlan);
    }

    public UserActionPlan updateActionPlanStatus(Long userId, Long actionPlanId,
            UserActionPlan.Status status) {
        List<UserActionPlan> userActionPlans = userActionPlanRepository.findByUserId(userId);
        UserActionPlan userActionPlan = userActionPlans.stream()
                .filter(plan -> plan.getActionPlanId().equals(actionPlanId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("您未采纳该行动计划"));

        userActionPlan.setStatus(status);
        if (status == UserActionPlan.Status.COMPLETED) {
            userActionPlan.setCompletedAt(LocalDateTime.now());
        }

        return userActionPlanRepository.save(userActionPlan);
    }

    public List<ActionPlanItem> generatePersonalizedActionPlans(Long userId) {
        return actionPlanGenerationService.generateActionPlans(userId);
    }
}