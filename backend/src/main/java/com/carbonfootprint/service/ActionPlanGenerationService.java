package com.carbonfootprint.service;

import com.carbonfootprint.dto.UserEmissionStats;
import com.carbonfootprint.entity.ActionPlanItem;
import com.carbonfootprint.entity.DietEmission;
import com.carbonfootprint.entity.ElectricityEmission;
import com.carbonfootprint.entity.FootprintSummary;
import com.carbonfootprint.entity.TransportEmission;
import com.carbonfootprint.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActionPlanGenerationService {

    private final TransportEmissionRepository transportEmissionRepository;
    private final DietEmissionRepository dietEmissionRepository;
    private final ElectricityEmissionRepository electricityEmissionRepository;
    private final FootprintSummaryRepository footprintSummaryRepository;
    private final ActionPlanRepository actionPlanRepository;

    private static final double AVG_TRANSPORT_EMISSION = 50.0;
    private static final double AVG_DIET_EMISSION = 80.0;
    private static final double AVG_ELECTRICITY_EMISSION = 120.0;

    public UserEmissionStats calculateUserEmissionStats(Long userId) {
        UserEmissionStats stats = new UserEmissionStats();
        stats.setUserId(userId);

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(30);

        List<TransportEmission> transportEmissions = transportEmissionRepository
                .findByUserIdAndEmissionDateBetween(userId, startDate, endDate);
        List<DietEmission> dietEmissions = dietEmissionRepository
                .findByUserIdAndEmissionDateBetween(userId, startDate, endDate);
        List<ElectricityEmission> electricityEmissions = electricityEmissionRepository
                .findByUserIdAndEmissionDateBetween(userId, startDate, endDate);

        double totalTransport = transportEmissions.stream().mapToDouble(TransportEmission::getEmissionAmount).sum();
        double totalDiet = dietEmissions.stream().mapToDouble(DietEmission::getEmissionAmount).sum();
        double totalElectricity = electricityEmissions.stream().mapToDouble(ElectricityEmission::getEmissionAmount).sum();

        stats.setTotalTransportEmission(totalTransport);
        stats.setTotalDietEmission(totalDiet);
        stats.setTotalElectricityEmission(totalElectricity);
        stats.setTotalEmission(totalTransport + totalDiet + totalElectricity);

        stats.setAvgTransportEmission(AVG_TRANSPORT_EMISSION);
        stats.setAvgDietEmission(AVG_DIET_EMISSION);
        stats.setAvgElectricityEmission(AVG_ELECTRICITY_EMISSION);
        stats.setAvgTotalEmission(AVG_TRANSPORT_EMISSION + AVG_DIET_EMISSION + AVG_ELECTRICITY_EMISSION);

        analyzeTransportData(transportEmissions, stats);
        analyzeDietData(dietEmissions, stats);
        analyzeElectricityData(electricityEmissions, stats);

        return stats;
    }

    private void analyzeTransportData(List<TransportEmission> emissions, UserEmissionStats stats) {
        if (emissions.isEmpty()) {
            return;
        }

        long carUsageCount = emissions.stream()
                .filter(e -> e.getTransportType() != null)
                .filter(e -> {
                    try {
                        int type = Integer.parseInt(e.getTransportType());
                        return type >= 5 && type <= 14;
                    } catch (NumberFormatException ex) {
                        return false;
                    }
                })
                .count();
        long shortTripCount = emissions.stream()
                .filter(e -> e.getDistance() != null && e.getDistance() < 3.0)
                .count();

        stats.setCarUsageCount((int) carUsageCount);
        stats.setCarUsagePercentage((double) carUsageCount / emissions.size() * 100);
        stats.setShortTripCount((int) shortTripCount);
        stats.setShortTripPercentage((double) shortTripCount / emissions.size() * 100);
    }

    private void analyzeDietData(List<DietEmission> emissions, UserEmissionStats stats) {
        if (emissions.isEmpty()) {
            return;
        }

        double redMeatEmission = emissions.stream()
                .filter(e -> e.getFoodType() != null)
                .filter(e -> {
                    try {
                        int type = Integer.parseInt(e.getFoodType());
                        return type >= 0 && type <= 2;
                    } catch (NumberFormatException ex) {
                        return false;
                    }
                })
                .mapToDouble(DietEmission::getEmissionAmount).sum();
        double totalDietEmission = emissions.stream().mapToDouble(DietEmission::getEmissionAmount).sum();

        stats.setRedMeatConsumption(redMeatEmission);
        stats.setRedMeatPercentage(totalDietEmission > 0 ? redMeatEmission / totalDietEmission * 100 : 0);

        double foodWaste = emissions.stream()
                .filter(e -> e.getDescription() != null && e.getDescription().contains("waste"))
                .mapToDouble(DietEmission::getAmount).sum();
        stats.setFoodWasteAmount(foodWaste);
    }

    private void analyzeElectricityData(List<ElectricityEmission> emissions, UserEmissionStats stats) {
        if (emissions.isEmpty()) {
            return;
        }

        double peakHourEmission = emissions.stream()
                .filter(e -> e.getDescription() != null && e.getDescription().contains("peak"))
                .mapToDouble(ElectricityEmission::getEmissionAmount).sum();
        double totalElectricityEmission = emissions.stream().mapToDouble(ElectricityEmission::getEmissionAmount).sum();

        stats.setPeakHourElectricityUsage(peakHourEmission);
        stats.setPeakHourPercentage(totalElectricityEmission > 0 ? peakHourEmission / totalElectricityEmission * 100 : 0);
    }

    public List<ActionPlanItem> generateActionPlans(Long userId) {
        UserEmissionStats stats = calculateUserEmissionStats(userId);
        List<ActionPlanItem> actionPlans = new ArrayList<>();

        actionPlans.addAll(generateTransportActionPlans(stats));
        actionPlans.addAll(generateDietActionPlans(stats));
        actionPlans.addAll(generateElectricityActionPlans(stats));

        return actionPlans;
    }

    private List<ActionPlanItem> generateTransportActionPlans(UserEmissionStats stats) {
        List<ActionPlanItem> actionPlans = new ArrayList<>();

        if (stats.isHighCarUsage()) {
            double reduction = stats.getTotalTransportEmission() * 0.3;
            ActionPlanItem plan = createActionPlan(
                    ActionPlanItem.Category.TRANSPORT,
                    "尝试每周使用公共交通2次",
                    String.format("您的汽车使用频率较高（%.1f%%），建议每周使用公共交通2次，预计每月可减少排放%.2f kg CO₂e",
                            stats.getCarUsagePercentage(), reduction),
                    reduction,
                    ActionPlanItem.Difficulty.MEDIUM,
                    ActionPlanItem.Cost.LOW);
            actionPlans.add(plan);
        }

        if (stats.hasManyShortTrips()) {
            double reduction = stats.getShortTripCount() * 0.21 * 3.0;
            ActionPlanItem plan = createActionPlan(
                    ActionPlanItem.Category.TRANSPORT,
                    "3公里内建议步行或骑行",
                    String.format("您有%d次短途出行（3公里内），建议步行或骑行，预计每月可减少排放%.2f kg CO₂e",
                            stats.getShortTripCount(), reduction),
                    reduction,
                    ActionPlanItem.Difficulty.LOW,
                    ActionPlanItem.Cost.LOW);
            actionPlans.add(plan);
        }

        if (stats.isTransportAboveAverage()) {
            double reduction = (stats.getTotalTransportEmission() - stats.getAvgTransportEmission()) * 0.4;
            ActionPlanItem plan = createActionPlan(
                    ActionPlanItem.Category.TRANSPORT,
                    "优化出行方式，减少碳足迹",
                    String.format("您的交通排放%.2f kg高于平均值%.2f kg，建议优化出行方式，预计可减少排放%.2f kg CO₂e",
                            stats.getTotalTransportEmission(), stats.getAvgTransportEmission(), reduction),
                    reduction,
                    ActionPlanItem.Difficulty.MEDIUM,
                    ActionPlanItem.Cost.MEDIUM);
            actionPlans.add(plan);
        }

        return actionPlans;
    }

    private List<ActionPlanItem> generateDietActionPlans(UserEmissionStats stats) {
        List<ActionPlanItem> actionPlans = new ArrayList<>();

        if (stats.isHighRedMeatConsumption()) {
            double reduction = stats.getTotalDietEmission() * 0.2;
            ActionPlanItem plan = createActionPlan(
                    ActionPlanItem.Category.DIET,
                    "每周增加2顿素食",
                    String.format("您的红肉摄入占比%.1f%%较高，建议每周增加2顿素食，预计每月可减少排放%.2f kg CO₂e",
                            stats.getRedMeatPercentage(), reduction),
                    reduction,
                    ActionPlanItem.Difficulty.LOW,
                    ActionPlanItem.Cost.LOW);
            actionPlans.add(plan);
        }

        if (stats.hasFoodWaste()) {
            double reduction = stats.getFoodWasteAmount() * 15.0;
            ActionPlanItem plan = createActionPlan(
                    ActionPlanItem.Category.DIET,
                    "合理规划采购，减少食物浪费",
                    String.format("您存在食物浪费情况，建议合理规划采购，预计每月可减少排放%.2f kg CO₂e", reduction),
                    reduction,
                    ActionPlanItem.Difficulty.LOW,
                    ActionPlanItem.Cost.LOW);
            actionPlans.add(plan);
        }

        if (stats.isDietAboveAverage()) {
            double reduction = (stats.getTotalDietEmission() - stats.getAvgDietEmission()) * 0.3;
            ActionPlanItem plan = createActionPlan(
                    ActionPlanItem.Category.DIET,
                    "调整饮食结构，选择低碳食物",
                    String.format("您的饮食排放%.2f kg高于平均值%.2f kg，建议选择更多蔬菜和低碳食物，预计可减少排放%.2f kg CO₂e",
                            stats.getTotalDietEmission(), stats.getAvgDietEmission(), reduction),
                    reduction,
                    ActionPlanItem.Difficulty.MEDIUM,
                    ActionPlanItem.Cost.LOW);
            actionPlans.add(plan);
        }

        return actionPlans;
    }

    private List<ActionPlanItem> generateElectricityActionPlans(UserEmissionStats stats) {
        List<ActionPlanItem> actionPlans = new ArrayList<>();

        if (stats.isHighPeakHourUsage()) {
            double reduction = stats.getPeakHourElectricityUsage() * 0.2;
            ActionPlanItem plan = createActionPlan(
                    ActionPlanItem.Category.ELECTRICITY,
                    "错峰用电，使用节能电器",
                    String.format("您的高峰用电占比%.1f%%较高，建议错峰用电并使用节能电器，预计每月可减少排放%.2f kg CO₂e",
                            stats.getPeakHourPercentage(), reduction),
                    reduction,
                    ActionPlanItem.Difficulty.LOW,
                    ActionPlanItem.Cost.MEDIUM);
            actionPlans.add(plan);
        }

        if (stats.isElectricityAboveAverage()) {
            double reduction = (stats.getTotalElectricityEmission() - stats.getAvgElectricityEmission()) * 0.35;
            ActionPlanItem plan = createActionPlan(
                    ActionPlanItem.Category.ELECTRICITY,
                    "提高用电效率，节约能源",
                    String.format("您的用电排放%.2f kg高于平均值%.2f kg，建议提高用电效率，预计可减少排放%.2f kg CO₂e",
                            stats.getTotalElectricityEmission(), stats.getAvgElectricityEmission(), reduction),
                    reduction,
                    ActionPlanItem.Difficulty.MEDIUM,
                    ActionPlanItem.Cost.MEDIUM);
            actionPlans.add(plan);
        }

        ActionPlanItem plan = createActionPlan(
                ActionPlanItem.Category.ELECTRICITY,
                "使用LED灯泡和智能插座",
                "将传统灯泡替换为LED灯泡，使用智能插座管理待机功耗，预计每月可减少排放5-10 kg CO₂e",
                7.5,
                ActionPlanItem.Difficulty.LOW,
                ActionPlanItem.Cost.LOW);
        actionPlans.add(plan);

        return actionPlans;
    }

    private ActionPlanItem createActionPlan(ActionPlanItem.Category category, String title,
            String description, double impact,
            ActionPlanItem.Difficulty difficulty, ActionPlanItem.Cost cost) {
        ActionPlanItem actionPlan = new ActionPlanItem();
        actionPlan.setCategory(category);
        actionPlan.setTitle(title);
        actionPlan.setDescription(description);
        actionPlan.setImpact(impact);
        actionPlan.setDifficulty(difficulty);
        actionPlan.setCost(cost);
        return actionPlan;
    }

    public Map<String, List<ActionPlanItem>> getActionPlansByCategory(Long userId) {
        List<ActionPlanItem> actionPlans = generateActionPlans(userId);
        return actionPlans.stream().collect(Collectors.groupingBy(plan -> plan.getCategory().name()));
    }
}