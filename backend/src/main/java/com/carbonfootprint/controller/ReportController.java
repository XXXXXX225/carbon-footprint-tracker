package com.carbonfootprint.controller;

import com.carbonfootprint.entity.FootprintSummary;
import com.carbonfootprint.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
@Tag(name = "数据报表", description = "碳足迹汇总和报表相关接口")
public class ReportController {
    
    private final ReportService reportService;
    
    /**
     * 获取用户的排放汇总
     * @param authentication 认证信息
     * @param period 时间段类型（DAILY, WEEKLY, MONTHLY, YEARLY）
     * @param date 日期（默认为今天）
     * @return 碳足迹汇总
     */
    @GetMapping("/summary")
    @Operation(summary = "获取排放汇总", description = "获取用户指定时间段的碳足迹汇总数据")
    public ResponseEntity<FootprintSummary> getEmissionSummary(
            Authentication authentication, 
            @RequestParam(defaultValue = "MONTHLY") FootprintSummary.Period period,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        Long userId = getUserIdFromAuthentication(authentication);
        if (date == null) {
            date = LocalDate.now();
        }
        FootprintSummary summary = reportService.generateFootprintSummary(userId, period, date);
        return ResponseEntity.ok(summary);
    }
    
    /**
     * 获取用户的碳足迹历史记录
     * @param authentication 认证信息
     * @param period 时间段类型（DAILY, WEEKLY, MONTHLY, YEARLY）
     * @return 碳足迹汇总列表
     */
    @GetMapping("/history")
    @Operation(summary = "获取碳足迹历史", description = "获取用户指定时间段类型的碳足迹历史记录")
    public ResponseEntity<List<FootprintSummary>> getFootprintHistory(
            Authentication authentication, 
            @RequestParam(defaultValue = "MONTHLY") FootprintSummary.Period period
    ) {
        Long userId = getUserIdFromAuthentication(authentication);
        List<FootprintSummary> history = reportService.getFootprintHistory(userId, period);
        return ResponseEntity.ok(history);
    }
    
    /**
     * 获取用户的最新碳足迹汇总
     * @param authentication 认证信息
     * @param period 时间段类型（DAILY, WEEKLY, MONTHLY, YEARLY）
     * @return 碳足迹汇总
     */
    @GetMapping("/latest")
    @Operation(summary = "获取最新碳足迹汇总", description = "获取用户指定时间段类型的最新碳足迹汇总数据")
    public ResponseEntity<FootprintSummary> getLatestFootprintSummary(
            Authentication authentication, 
            @RequestParam(defaultValue = "MONTHLY") FootprintSummary.Period period
    ) {
        Long userId = getUserIdFromAuthentication(authentication);
        FootprintSummary summary = reportService.getLatestFootprintSummary(userId, period);
        return ResponseEntity.ok(summary);
    }
    
    /**
     * 从认证信息中获取用户ID
     * @param authentication 认证信息
     * @return 用户ID
     */
    private Long getUserIdFromAuthentication(Authentication authentication) {
        com.carbonfootprint.entity.User user = (com.carbonfootprint.entity.User) authentication.getPrincipal();
        return user.getId();
    }
}
