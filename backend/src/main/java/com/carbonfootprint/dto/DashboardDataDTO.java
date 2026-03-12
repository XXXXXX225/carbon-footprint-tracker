package com.carbonfootprint.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDataDTO {
    private OverviewStats overview;
    private List<EmissionTrend> emissionTrends;
    private List<CategoryDistribution> categoryDistribution;
    private List<TopUser> topUsers;
    private List<RegionalStats> regionalStats;
    private List<RealTimeActivity> realTimeActivities;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OverviewStats {
        private Long totalUsers;
        private Double totalEmission;
        private Double totalReduction;
        private Long totalPoints;
        private Double avgDailyEmission;
        private Integer activeUsersToday;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmissionTrend {
        private String date;
        private Double emission;
        private Double transportEmission;
        private Double dietEmission;
        private Double electricityEmission;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryDistribution {
        private String category;
        private Double value;
        private Double percentage;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopUser {
        private Long userId;
        private String username;
        private Integer totalPoints;
        private Double totalReduction;
        private Integer rank;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegionalStats {
        private String region;
        private Long userCount;
        private Double totalEmission;
        private Double avgEmission;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RealTimeActivity {
        private String time;
        private String username;
        private String activity;
        private Double emission;
        private String type;
    }
}
