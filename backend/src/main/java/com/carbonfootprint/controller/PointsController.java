package com.carbonfootprint.controller;

import com.carbonfootprint.entity.PointsRecord;
import com.carbonfootprint.service.PointsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/points")
public class PointsController {
    
    @Autowired
    private PointsService pointsService;
    
    /**
     * 获取当前用户的总积分
     * @return 总积分
     */
    @GetMapping("/total")
    public ResponseEntity<Integer> getTotalPoints() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.parseLong(auth.getName());
        
        Integer totalPoints = pointsService.getUserTotalPoints(userId);
        return ResponseEntity.ok(totalPoints);
    }
    
    /**
     * 获取当前用户的积分记录
     * @return 积分记录列表
     */
    @GetMapping("/records")
    public ResponseEntity<List<PointsRecord>> getPointsRecords() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.parseLong(auth.getName());
        
        List<PointsRecord> records = pointsService.getUserPointsRecords(userId);
        return ResponseEntity.ok(records);
    }
    
    /**
     * 手动触发积分计算（用于测试）
     * @param emissionReduced 减少的碳排放量
     * @param reason 积分原因
     * @return 获得的积分
     */
    @PostMapping("/calculate")
    public ResponseEntity<Integer> calculatePoints(
            @RequestParam Double emissionReduced,
            @RequestParam String reason) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.parseLong(auth.getName());
        
        Integer pointsEarned = pointsService.calculateAndAwardPoints(userId, emissionReduced, reason);
        return ResponseEntity.ok(pointsEarned);
    }
}
