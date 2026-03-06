package com.carbonfootprint.dto;

import lombok.Data;

@Data
public class UserEmissionStats {
    
    private Long userId;
    
    private Double totalTransportEmission;
    private Double totalDietEmission;
    private Double totalElectricityEmission;
    private Double totalEmission;
    
    private Double avgTransportEmission;
    private Double avgDietEmission;
    private Double avgElectricityEmission;
    private Double avgTotalEmission;
    
    private Integer carUsageCount;
    private Double carUsagePercentage;
    private Integer shortTripCount;
    private Double shortTripPercentage;
    
    private Double redMeatConsumption;
    private Double redMeatPercentage;
    private Double foodWasteAmount;
    
    private Double peakHourElectricityUsage;
    private Double peakHourPercentage;
    
    public boolean isHighCarUsage() {
        return carUsagePercentage != null && carUsagePercentage > 60;
    }
    
    public boolean hasManyShortTrips() {
        return shortTripPercentage != null && shortTripPercentage > 40;
    }
    
    public boolean isHighRedMeatConsumption() {
        return redMeatPercentage != null && redMeatPercentage > 50;
    }
    
    public boolean hasFoodWaste() {
        return foodWasteAmount != null && foodWasteAmount > 0;
    }
    
    public boolean isHighPeakHourUsage() {
        return peakHourPercentage != null && peakHourPercentage > 40;
    }
    
    public boolean isTransportAboveAverage() {
        return avgTransportEmission != null && totalTransportEmission > avgTransportEmission;
    }
    
    public boolean isDietAboveAverage() {
        return avgDietEmission != null && totalDietEmission > avgDietEmission;
    }
    
    public boolean isElectricityAboveAverage() {
        return avgElectricityEmission != null && totalElectricityEmission > avgElectricityEmission;
    }
}
