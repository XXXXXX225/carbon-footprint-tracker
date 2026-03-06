package com.carbonfootprint.service;

import com.carbonfootprint.strategy.EmissionCalculationStrategy;
import com.carbonfootprint.strategy.EmissionStrategyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CarbonEmissionCalculator {
    
    private final EmissionStrategyFactory strategyFactory;
    
    @Autowired
    public CarbonEmissionCalculator(EmissionStrategyFactory strategyFactory) {
        this.strategyFactory = strategyFactory;
    }
    
    public double calculateTransportEmission(int transportType, double distance) {
        EmissionCalculationStrategy strategy = strategyFactory.getStrategy("transport");
        return strategy.calculate(transportType, distance);
    }
    
    public double calculateDietEmission(int foodType, double weight) {
        EmissionCalculationStrategy strategy = strategyFactory.getStrategy("diet");
        return strategy.calculate(foodType, weight);
    }
    
    public double calculateElectricityEmission(double electricityAmount) {
        EmissionCalculationStrategy strategy = strategyFactory.getStrategy("electricity");
        return strategy.calculate(electricityAmount);
    }
    
    public double calculateElectricityAmount(double power, double usageTime, int usageDays) {
        if (power < 0) {
            throw new IllegalArgumentException("功率不能为负数");
        }
        if (usageTime < 0) {
            throw new IllegalArgumentException("使用时间不能为负数");
        }
        if (usageDays < 0) {
            throw new IllegalArgumentException("使用天数不能为负数");
        }
        return (power * usageTime * usageDays) / 1000.0;
    }
}
