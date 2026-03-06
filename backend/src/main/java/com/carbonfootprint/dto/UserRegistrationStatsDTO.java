package com.carbonfootprint.dto;

import java.util.Map;

public class UserRegistrationStatsDTO {
    private long totalUsers;
    private long todayNewUsers;
    private long weekNewUsers;
    private long monthNewUsers;
    private Map<String, Long> roleDistribution;
    private Map<String, Long> dailyRegistrationTrend;

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getTodayNewUsers() {
        return todayNewUsers;
    }

    public void setTodayNewUsers(long todayNewUsers) {
        this.todayNewUsers = todayNewUsers;
    }

    public long getWeekNewUsers() {
        return weekNewUsers;
    }

    public void setWeekNewUsers(long weekNewUsers) {
        this.weekNewUsers = weekNewUsers;
    }

    public long getMonthNewUsers() {
        return monthNewUsers;
    }

    public void setMonthNewUsers(long monthNewUsers) {
        this.monthNewUsers = monthNewUsers;
    }

    public Map<String, Long> getRoleDistribution() {
        return roleDistribution;
    }

    public void setRoleDistribution(Map<String, Long> roleDistribution) {
        this.roleDistribution = roleDistribution;
    }

    public Map<String, Long> getDailyRegistrationTrend() {
        return dailyRegistrationTrend;
    }

    public void setDailyRegistrationTrend(Map<String, Long> dailyRegistrationTrend) {
        this.dailyRegistrationTrend = dailyRegistrationTrend;
    }
}
