package com.carbonfootprint.service;

import com.carbonfootprint.dto.DashboardDataDTO;
import com.carbonfootprint.entity.*;
import com.carbonfootprint.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserRepository userRepository;
    private final TransportEmissionRepository transportEmissionRepository;
    private final DietEmissionRepository dietEmissionRepository;
    private final ElectricityEmissionRepository electricityEmissionRepository;
    private final PointsRecordRepository pointsRecordRepository;

    public DashboardDataDTO getDashboardData() {
        DashboardDataDTO dashboard = new DashboardDataDTO();
        
        dashboard.setOverview(getOverviewStats());
        dashboard.setEmissionTrends(getEmissionTrends());
        dashboard.setCategoryDistribution(getCategoryDistribution());
        dashboard.setTopUsers(getTopUsers());
        dashboard.setRegionalStats(getRegionalStats());
        dashboard.setRealTimeActivities(getRealTimeActivities());
        
        return dashboard;
    }

    private DashboardDataDTO.OverviewStats getOverviewStats() {
        long totalUsers = userRepository.count();
        
        LocalDate today = LocalDate.now();
        LocalDate monthStart = today.withDayOfMonth(1);
        
        double totalTransport = transportEmissionRepository.findAll()
                .stream()
                .mapToDouble(TransportEmission::getEmissionAmount)
                .sum();
        
        double totalDiet = dietEmissionRepository.findAll()
                .stream()
                .mapToDouble(DietEmission::getEmissionAmount)
                .sum();
        
        double totalElectricity = electricityEmissionRepository.findAll()
                .stream()
                .mapToDouble(ElectricityEmission::getEmissionAmount)
                .sum();
        
        double totalEmission = totalTransport + totalDiet + totalElectricity;
        
        long totalPoints = pointsRecordRepository.findAll()
                .stream()
                .mapToLong(PointsRecord::getPointsChange)
                .sum();
        
        double totalReduction = pointsRecordRepository.findAll()
                .stream()
                .mapToDouble(PointsRecord::getEmissionReduced)
                .sum();
        
        double avgDailyEmission = totalUsers > 0 ? totalEmission / totalUsers / 30 : 0;
        
        long activeUsersToday = transportEmissionRepository.findByEmissionDate(today).stream()
                .map(TransportEmission::getUserId)
                .distinct()
                .count();
        
        return new DashboardDataDTO.OverviewStats(
                totalUsers,
                Math.round(totalEmission * 100.0) / 100.0,
                Math.round(totalReduction * 100.0) / 100.0,
                totalPoints,
                Math.round(avgDailyEmission * 100.0) / 100.0,
                (int) activeUsersToday
        );
    }

    private List<DashboardDataDTO.EmissionTrend> getEmissionTrends() {
        List<DashboardDataDTO.EmissionTrend> trends = new ArrayList<>();
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        
        for (int i = 29; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            
            double transportEmission = transportEmissionRepository.findByEmissionDate(date)
                    .stream()
                    .mapToDouble(TransportEmission::getEmissionAmount)
                    .sum();
            
            double dietEmission = dietEmissionRepository.findByEmissionDate(date)
                    .stream()
                    .mapToDouble(DietEmission::getEmissionAmount)
                    .sum();
            
            double electricityEmission = electricityEmissionRepository.findByEmissionDate(date)
                    .stream()
                    .mapToDouble(ElectricityEmission::getEmissionAmount)
                    .sum();
            
            double totalEmission = transportEmission + dietEmission + electricityEmission;
            
            trends.add(new DashboardDataDTO.EmissionTrend(
                    date.format(formatter),
                    Math.round(totalEmission * 100.0) / 100.0,
                    Math.round(transportEmission * 100.0) / 100.0,
                    Math.round(dietEmission * 100.0) / 100.0,
                    Math.round(electricityEmission * 100.0) / 100.0
            ));
        }
        
        return trends;
    }

    private List<DashboardDataDTO.CategoryDistribution> getCategoryDistribution() {
        double totalTransport = transportEmissionRepository.findAll()
                .stream()
                .mapToDouble(TransportEmission::getEmissionAmount)
                .sum();
        
        double totalDiet = dietEmissionRepository.findAll()
                .stream()
                .mapToDouble(DietEmission::getEmissionAmount)
                .sum();
        
        double totalElectricity = electricityEmissionRepository.findAll()
                .stream()
                .mapToDouble(ElectricityEmission::getEmissionAmount)
                .sum();
        
        double total = totalTransport + totalDiet + totalElectricity;
        
        List<DashboardDataDTO.CategoryDistribution> distribution = new ArrayList<>();
        
        if (total > 0) {
            distribution.add(new DashboardDataDTO.CategoryDistribution(
                    "交通出行",
                    Math.round(totalTransport * 100.0) / 100.0,
                    Math.round(totalTransport / total * 10000.0) / 100.0
            ));
            
            distribution.add(new DashboardDataDTO.CategoryDistribution(
                    "饮食消费",
                    Math.round(totalDiet * 100.0) / 100.0,
                    Math.round(totalDiet / total * 10000.0) / 100.0
            ));
            
            distribution.add(new DashboardDataDTO.CategoryDistribution(
                    "用电能耗",
                    Math.round(totalElectricity * 100.0) / 100.0,
                    Math.round(totalElectricity / total * 10000.0) / 100.0
            ));
        }
        
        return distribution;
    }

    private List<DashboardDataDTO.TopUser> getTopUsers() {
        List<User> topUsers = userRepository.findAll(
                PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "totalPoints"))
        ).getContent();
        
        List<DashboardDataDTO.TopUser> result = new ArrayList<>();
        int rank = 1;
        
        for (User user : topUsers) {
            double totalReduction = pointsRecordRepository.findByUserId(user.getId())
                    .stream()
                    .mapToDouble(PointsRecord::getEmissionReduced)
                    .sum();
            
            result.add(new DashboardDataDTO.TopUser(
                    user.getId(),
                    user.getUsername(),
                    user.getTotalPoints(),
                    Math.round(totalReduction * 100.0) / 100.0,
                    rank++
            ));
        }
        
        return result;
    }

    private List<DashboardDataDTO.RegionalStats> getRegionalStats() {
        List<DashboardDataDTO.RegionalStats> stats = new ArrayList<>();
        
        String[] regions = {"北京", "上海", "广州", "深圳", "杭州", "成都", "武汉", "西安"};
        Random random = new Random();
        
        for (String region : regions) {
            long userCount = 50 + random.nextInt(200);
            double totalEmission = 100 + random.nextDouble() * 500;
            double avgEmission = totalEmission / userCount;
            
            stats.add(new DashboardDataDTO.RegionalStats(
                    region,
                    userCount,
                    Math.round(totalEmission * 100.0) / 100.0,
                    Math.round(avgEmission * 100.0) / 100.0
            ));
        }
        
        return stats.stream()
                .sorted(Comparator.comparing(DashboardDataDTO.RegionalStats::getUserCount).reversed())
                .collect(Collectors.toList());
    }

    private List<DashboardDataDTO.RealTimeActivity> getRealTimeActivities() {
        List<DashboardDataDTO.RealTimeActivity> activities = new ArrayList<>();
        
        List<User> users = userRepository.findAll(PageRequest.of(0, 20)).getContent();
        String[] activityTypes = {"交通出行", "饮食记录", "用电记录"};
        String[] transportTypes = {"步行", "骑行", "公共汽车", "地铁", "出租车", "私家车"};
        String[] foodTypes = {"牛肉", "猪肉", "鸡肉", "蔬菜", "水果"};
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        
        Random random = new Random();
        
        for (int i = 0; i < 10; i++) {
            User user = users.get(random.nextInt(users.size()));
            String type = activityTypes[random.nextInt(activityTypes.length)];
            String activity;
            double emission;
            
            switch (type) {
                case "交通出行":
                    String transport = transportTypes[random.nextInt(transportTypes.length)];
                    activity = "使用" + transport + "出行";
                    emission = 0.5 + random.nextDouble() * 5;
                    break;
                case "饮食记录":
                    String food = foodTypes[random.nextInt(foodTypes.length)];
                    activity = "食用" + food;
                    emission = 0.1 + random.nextDouble() * 2;
                    break;
                case "用电记录":
                    activity = "使用电器";
                    emission = 0.2 + random.nextDouble() * 3;
                    break;
                default:
                    activity = "未知活动";
                    emission = 0;
            }
            
            LocalDateTime time = LocalDateTime.now().minusMinutes(random.nextInt(60));
            
            activities.add(new DashboardDataDTO.RealTimeActivity(
                    time.format(formatter),
                    user.getUsername(),
                    activity,
                    Math.round(emission * 100.0) / 100.0,
                    type
            ));
        }
        
        return activities.stream()
                .sorted(Comparator.comparing(DashboardDataDTO.RealTimeActivity::getTime).reversed())
                .collect(Collectors.toList());
    }
}
