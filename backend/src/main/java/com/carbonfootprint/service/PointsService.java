package com.carbonfootprint.service;

import com.carbonfootprint.entity.PointsRecord;
import com.carbonfootprint.entity.PointsRule;
import com.carbonfootprint.entity.User;
import com.carbonfootprint.repository.PointsRecordRepository;
import com.carbonfootprint.repository.PointsRuleRepository;
import com.carbonfootprint.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PointsService {

    @Autowired
    private PointsRecordRepository pointsRecordRepository;

    @Autowired
    private PointsRuleRepository pointsRuleRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * 计算减碳积分
     * 
     * @param userId          用户ID
     * @param emissionReduced 减少的碳排放量（kg）
     * @param reason          积分原因
     * @return 获得的积分
     */
    @Transactional
    public Integer calculateAndAwardPoints(Long userId, Double emissionReduced, String reason) {
        // 获取当前有效的积分规则
        PointsRule rule = pointsRuleRepository.findByIsActiveTrue()
                .orElseGet(() -> {
                    // 如果没有规则，创建默认规则
                    PointsRule defaultRule = new PointsRule();
                    defaultRule.setName("默认规则");
                    defaultRule.setDescription("每减少1kg碳排放获得10积分");
                    defaultRule.setPointsPerKg(10.0);
                    defaultRule.setMinimumReduction(0.0);
                    defaultRule.setIsActive(true);
                    return pointsRuleRepository.save(defaultRule);
                });

        // 检查是否达到最小减碳量
        if (emissionReduced < rule.getMinimumReduction()) {
            return 0;
        }

        // 计算积分（四舍五入）
        int pointsEarned = (int) Math.round(emissionReduced * rule.getPointsPerKg());

        if (pointsEarned > 0) {
            // 获取用户信息
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("用户不存在"));

            // 计算新的总积分
            int newTotalPoints = user.getTotalPoints() + pointsEarned;

            // 更新用户总积分
            user.setTotalPoints(newTotalPoints);
            userRepository.save(user);

            // 记录积分变动
            PointsRecord record = new PointsRecord();
            record.setUserId(userId);
            record.setPointsChange(pointsEarned);
            record.setTotalPoints(newTotalPoints);
            record.setEmissionReduced(emissionReduced);
            record.setReason(reason);
            pointsRecordRepository.save(record);
        }

        return pointsEarned;
    }

    /**
     * 获取用户的积分记录
     * 
     * @param userId 用户ID
     * @return 积分记录列表
     */
    public List<PointsRecord> getUserPointsRecords(Long userId) {
        return pointsRecordRepository.findByUserId(userId);
    }

    /**
     * 获取用户的总积分
     * 
     * @param userId 用户ID
     * @return 总积分
     */
    public Integer getUserTotalPoints(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        return user.getTotalPoints();
    }
}
