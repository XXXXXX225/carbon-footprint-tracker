package com.carbonfootprint.service;

import com.carbonfootprint.dto.DietEmissionDTO;
import com.carbonfootprint.dto.ElectricityEmissionDTO;
import com.carbonfootprint.dto.EmissionsSummaryDTO;
import com.carbonfootprint.dto.TransportEmissionDTO;
import com.carbonfootprint.entity.DietEmission;
import com.carbonfootprint.entity.ElectricityEmission;
import com.carbonfootprint.entity.TransportEmission;
import com.carbonfootprint.repository.DietEmissionRepository;
import com.carbonfootprint.repository.ElectricityEmissionRepository;
import com.carbonfootprint.repository.TransportEmissionRepository;
import com.carbonfootprint.service.PointsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmissionService {

        private final CarbonEmissionCalculator emissionCalculator;
        private final TransportEmissionRepository transportEmissionRepository;
        private final DietEmissionRepository dietEmissionRepository;
        private final ElectricityEmissionRepository electricityEmissionRepository;
        private final PointsService pointsService;

        // 交通工具类型映射
        private static final String[] TRANSPORT_TYPES = {
                        "步行", "骑行", "公共汽车", "地铁", "出租车",
                        "小型汽车（汽油）", "小型汽车（柴油）", "小型汽车（电动）",
                        "中型汽车（汽油）", "中型汽车（柴油）", "中型汽车（电动）",
                        "大型汽车（汽油）", "大型汽车（柴油）", "大型汽车（电动）",
                        "飞机（短途）", "飞机（长途）", "火车（电力）", "火车（柴油）"
        };

        // 食物类型映射
        private static final String[] FOOD_TYPES = {
                        "牛肉", "羊肉", "猪肉", "鸡肉", "鱼肉",
                        "鸡蛋", "牛奶", "蔬菜", "水果", "谷物",
                        "豆类", "加工食品", "其他"
        };

        /**
         * 记录交通排放
         * 
         * @param userId 用户ID
         * @param dto    交通排放DTO
         * @return 交通排放记录
         */
        public TransportEmission recordTransportEmission(Long userId, TransportEmissionDTO dto) {
                double emissionAmount = emissionCalculator.calculateTransportEmission(dto.getTransportType(),
                                dto.getDistance());

                TransportEmission emission = new TransportEmission();
                emission.setUserId(userId);
                emission.setTransportType(dto.getTransportType());
                emission.setDistance(dto.getDistance());
                emission.setFuelType(dto.getFuelType());
                emission.setFuelConsumption(dto.getFuelConsumption());
                emission.setEmissionAmount(emissionAmount);
                emission.setEmissionDate(dto.getEmissionDate());
                emission.setDescription(dto.getDescription());

                TransportEmission savedEmission = transportEmissionRepository.save(emission);

                // 计算减碳量并颁发积分
                // 基准排放量：使用最环保的交通工具（步行或骑行）的排放量
                double baselineEmission = emissionCalculator.calculateTransportEmission(0, dto.getDistance());
                double emissionReduced = Math.max(0, emissionAmount - baselineEmission);

                if (emissionReduced > 0) {
                        pointsService.calculateAndAwardPoints(userId, emissionReduced, "交通出行减碳");
                }

                return savedEmission;
        }

        /**
         * 记录饮食排放
         * 
         * @param userId 用户ID
         * @param dto    饮食排放DTO
         * @return 饮食排放记录
         */
        public DietEmission recordDietEmission(Long userId, DietEmissionDTO dto) {
                double emissionAmount = emissionCalculator.calculateDietEmission(dto.getFoodType(), dto.getAmount());

                DietEmission emission = new DietEmission();
                emission.setUserId(userId);
                emission.setFoodType(dto.getFoodType());
                emission.setSpecificFood(dto.getSpecificFood());
                emission.setAmount(dto.getAmount());
                emission.setCookingMethod(dto.getCookingMethod());
                emission.setEmissionAmount(emissionAmount);
                emission.setEmissionDate(dto.getEmissionDate());
                emission.setDescription(dto.getDescription());

                DietEmission savedEmission = dietEmissionRepository.save(emission);

                // 计算减碳量并颁发积分
                // 基准排放量：使用最环保的食物（蔬菜）的排放量
                double baselineEmission = emissionCalculator.calculateDietEmission(7, dto.getAmount()); // 7对应蔬菜
                double emissionReduced = Math.max(0, emissionAmount - baselineEmission);

                if (emissionReduced > 0) {
                        pointsService.calculateAndAwardPoints(userId, emissionReduced, "饮食减碳");
                }

                return savedEmission;
        }

        /**
         * 记录用电排放
         * 
         * @param userId 用户ID
         * @param dto    用电排放DTO
         * @return 用电排放记录
         */
        public ElectricityEmission recordElectricityEmission(Long userId, ElectricityEmissionDTO dto) {
                double electricityAmount = emissionCalculator.calculateElectricityAmount(dto.getPower(),
                                dto.getUsageTime(),
                                dto.getUsageDays());
                double emissionAmount = emissionCalculator.calculateElectricityEmission(electricityAmount);

                ElectricityEmission emission = new ElectricityEmission();
                emission.setUserId(userId);
                emission.setDeviceType(dto.getDeviceType());
                emission.setPower(dto.getPower());
                emission.setUsageTime(dto.getUsageTime());
                emission.setUsageDays(dto.getUsageDays());
                emission.setElectricityAmount(electricityAmount);
                emission.setEmissionAmount(emissionAmount);
                emission.setEmissionDate(dto.getEmissionDate());
                emission.setDescription(dto.getDescription());

                ElectricityEmission savedEmission = electricityEmissionRepository.save(emission);

                // 计算减碳量并颁发积分
                // 基准排放量：假设使用高效设备的排放量（减少20%）
                double baselineEmission = emissionAmount * 0.8;
                double emissionReduced = Math.max(0, emissionAmount - baselineEmission);

                if (emissionReduced > 0) {
                        pointsService.calculateAndAwardPoints(userId, emissionReduced, "用电减碳");
                }

                return savedEmission;
        }

        /**
         * 获取用户的交通排放记录
         * 
         * @param userId 用户ID
         * @return 交通排放记录列表
         */
        public List<TransportEmission> getTransportEmissions(Long userId) {
                return transportEmissionRepository.findByUserId(userId);
        }

        /**
         * 获取用户的饮食排放记录
         * 
         * @param userId 用户ID
         * @return 饮食排放记录列表
         */
        public List<DietEmission> getDietEmissions(Long userId) {
                return dietEmissionRepository.findByUserId(userId);
        }

        /**
         * 获取用户的用电排放记录
         * 
         * @param userId 用户ID
         * @return 用电排放记录列表
         */
        public List<ElectricityEmission> getElectricityEmissions(Long userId) {
                return electricityEmissionRepository.findByUserId(userId);
        }

        /**
         * 获取用户的交通排放汇总
         * 
         * @param userId 用户ID
         * @param period 汇总周期（week/month/year）
         * @return 交通排放汇总
         */
        public EmissionsSummaryDTO getTransportEmissionSummary(Long userId, String period) {
                LocalDate endDate = LocalDate.now();
                LocalDate startDate = calculateStartDate(endDate, period);

                List<TransportEmission> emissions = transportEmissionRepository.findByUserIdAndEmissionDateBetween(
                                userId,
                                startDate, endDate);

                EmissionsSummaryDTO summary = new EmissionsSummaryDTO();
                summary.setUserId(userId);
                summary.setPeriod(period);
                summary.setRecordCount(emissions.size());

                double totalEmission = emissions.stream()
                                .mapToDouble(TransportEmission::getEmissionAmount)
                                .sum();

                summary.setTotalEmission(totalEmission);
                summary.setTransportEmission(totalEmission);
                summary.setDietEmission(0.0);
                summary.setElectricityEmission(0.0);

                int days = (int) startDate.until(endDate).getDays() + 1;
                summary.setAverageDailyEmission(totalEmission / days);

                return summary;
        }

        /**
         * 获取用户的总排放汇总
         * 
         * @param userId 用户ID
         * @param period 汇总周期（week/month/year）
         * @return 总排放汇总
         */
        public EmissionsSummaryDTO getEmissionsSummary(Long userId, String period) {
                LocalDate endDate = LocalDate.now();
                LocalDate startDate = calculateStartDate(endDate, period);

                List<TransportEmission> transportEmissions = transportEmissionRepository
                                .findByUserIdAndEmissionDateBetween(userId, startDate, endDate);
                List<DietEmission> dietEmissions = dietEmissionRepository.findByUserIdAndEmissionDateBetween(userId,
                                startDate,
                                endDate);
                List<ElectricityEmission> electricityEmissions = electricityEmissionRepository
                                .findByUserIdAndEmissionDateBetween(userId, startDate, endDate);

                EmissionsSummaryDTO summary = new EmissionsSummaryDTO();
                summary.setUserId(userId);
                summary.setPeriod(period);
                summary.setRecordCount(transportEmissions.size() + dietEmissions.size() + electricityEmissions.size());

                double transportEmission = transportEmissions.stream()
                                .mapToDouble(TransportEmission::getEmissionAmount)
                                .sum();

                double dietEmission = dietEmissions.stream()
                                .mapToDouble(DietEmission::getEmissionAmount)
                                .sum();

                double electricityEmission = electricityEmissions.stream()
                                .mapToDouble(ElectricityEmission::getEmissionAmount)
                                .sum();

                double totalEmission = transportEmission + dietEmission + electricityEmission;

                summary.setTotalEmission(totalEmission);
                summary.setTransportEmission(transportEmission);
                summary.setDietEmission(dietEmission);
                summary.setElectricityEmission(electricityEmission);

                int days = (int) startDate.until(endDate).getDays() + 1;
                summary.setAverageDailyEmission(totalEmission / days);

                return summary;
        }

        /**
         * 根据周期计算开始日期
         * 
         * @param endDate 结束日期
         * @param period  周期
         * @return 开始日期
         */
        private LocalDate calculateStartDate(LocalDate endDate, String period) {
                return switch (period.toLowerCase()) {
                        case "week" -> endDate.minusWeeks(1);
                        case "month" -> endDate.minusMonths(1);
                        case "year" -> endDate.minusYears(1);
                        default -> endDate.minusWeeks(1); // 默认一周
                };
        }
}
