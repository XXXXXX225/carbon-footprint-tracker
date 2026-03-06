package com.carbonfootprint.service;

import com.carbonfootprint.entity.FootprintSummary;
import com.carbonfootprint.entity.TransportEmission;
import com.carbonfootprint.entity.DietEmission;
import com.carbonfootprint.entity.ElectricityEmission;
import com.carbonfootprint.repository.FootprintSummaryRepository;
import com.carbonfootprint.repository.TransportEmissionRepository;
import com.carbonfootprint.repository.DietEmissionRepository;
import com.carbonfootprint.repository.ElectricityEmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {
    
    private final FootprintSummaryRepository summaryRepository;
    private final TransportEmissionRepository transportRepository;
    private final DietEmissionRepository dietRepository;
    private final ElectricityEmissionRepository electricityRepository;
    
    /**
     * 生成用户的碳足迹汇总报告
     * @param userId 用户ID
     * @param period 时间段类型
     * @param date 日期
     * @return 碳足迹汇总
     */
    public FootprintSummary generateFootprintSummary(Long userId, FootprintSummary.Period period, LocalDate date) {
        LocalDate startDate = getPeriodStartDate(period, date);
        LocalDate endDate = getPeriodEndDate(period, date);
        
        // 查找是否已存在该时间段的汇总
        return summaryRepository.findByUserIdAndPeriodAndPeriodStartDateAndPeriodEndDate(
                userId, period, startDate, endDate
        ).orElseGet(() -> calculateAndSaveSummary(userId, period, startDate, endDate));
    }
    
    /**
     * 计算并保存碳足迹汇总
     * @param userId 用户ID
     * @param period 时间段类型
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 碳足迹汇总
     */
    private FootprintSummary calculateAndSaveSummary(Long userId, FootprintSummary.Period period, LocalDate startDate, LocalDate endDate) {
        // 计算各类型排放量
        double transportEmission = calculateTransportEmission(userId, startDate, endDate);
        double dietEmission = calculateDietEmission(userId, startDate, endDate);
        double electricityEmission = calculateElectricityEmission(userId, startDate, endDate);
        double totalEmission = transportEmission + dietEmission + electricityEmission;
        
        // 创建汇总记录
        FootprintSummary summary = new FootprintSummary();
        summary.setUserId(userId);
        summary.setPeriod(period);
        summary.setPeriodStartDate(startDate);
        summary.setPeriodEndDate(endDate);
        summary.setTransportEmission(transportEmission);
        summary.setDietEmission(dietEmission);
        summary.setElectricityEmission(electricityEmission);
        summary.setTotalEmission(totalEmission);
        
        return summaryRepository.save(summary);
    }
    
    /**
     * 计算交通排放量
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 交通排放量
     */
    private double calculateTransportEmission(Long userId, LocalDate startDate, LocalDate endDate) {
        List<TransportEmission> emissions = transportRepository.findByUserIdAndEmissionDateBetween(userId, startDate, endDate);
        return emissions.stream().mapToDouble(TransportEmission::getEmissionAmount).sum();
    }
    
    /**
     * 计算饮食排放量
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 饮食排放量
     */
    private double calculateDietEmission(Long userId, LocalDate startDate, LocalDate endDate) {
        List<DietEmission> emissions = dietRepository.findByUserIdAndEmissionDateBetween(userId, startDate, endDate);
        return emissions.stream().mapToDouble(DietEmission::getEmissionAmount).sum();
    }
    
    /**
     * 计算用电排放量
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 用电排放量
     */
    private double calculateElectricityEmission(Long userId, LocalDate startDate, LocalDate endDate) {
        List<ElectricityEmission> emissions = electricityRepository.findByUserIdAndEmissionDateBetween(userId, startDate, endDate);
        return emissions.stream().mapToDouble(ElectricityEmission::getEmissionAmount).sum();
    }
    
    /**
     * 获取时间段的开始日期
     * @param period 时间段类型
     * @param date 日期
     * @return 开始日期
     */
    private LocalDate getPeriodStartDate(FootprintSummary.Period period, LocalDate date) {
        switch (period) {
            case DAILY:
                return date;
            case WEEKLY:
                return date.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
            case MONTHLY:
                return date.withDayOfMonth(1);
            case YEARLY:
                return date.withDayOfYear(1);
            default:
                throw new IllegalArgumentException("无效的时间段类型");
        }
    }
    
    /**
     * 获取时间段的结束日期
     * @param period 时间段类型
     * @param date 日期
     * @return 结束日期
     */
    private LocalDate getPeriodEndDate(FootprintSummary.Period period, LocalDate date) {
        switch (period) {
            case DAILY:
                return date;
            case WEEKLY:
                return date.with(TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SUNDAY));
            case MONTHLY:
                return date.with(TemporalAdjusters.lastDayOfMonth());
            case YEARLY:
                return date.with(TemporalAdjusters.lastDayOfYear());
            default:
                throw new IllegalArgumentException("无效的时间段类型");
        }
    }
    
    /**
     * 获取用户的碳足迹历史记录
     * @param userId 用户ID
     * @param period 时间段类型
     * @return 碳足迹汇总列表
     */
    public List<FootprintSummary> getFootprintHistory(Long userId, FootprintSummary.Period period) {
        return summaryRepository.findByUserIdAndPeriodOrderByPeriodStartDateDesc(userId, period);
    }
    
    /**
     * 获取用户的最新碳足迹汇总
     * @param userId 用户ID
     * @param period 时间段类型
     * @return 碳足迹汇总
     */
    public FootprintSummary getLatestFootprintSummary(Long userId, FootprintSummary.Period period) {
        LocalDate now = LocalDate.now();
        return generateFootprintSummary(userId, period, now);
    }
}
