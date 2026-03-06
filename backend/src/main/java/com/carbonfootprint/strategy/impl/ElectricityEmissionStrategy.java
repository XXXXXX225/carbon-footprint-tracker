package com.carbonfootprint.strategy.impl;

import com.carbonfootprint.strategy.EmissionCalculationStrategy;
import org.springframework.stereotype.Component;

@Component
public class ElectricityEmissionStrategy implements EmissionCalculationStrategy {
    
    private static final double ELECTRICITY_EMISSION_FACTOR = 0.78;
    
    @Override
    public double calculate(double... params) {
        if (params.length < 1) {
            throw new IllegalArgumentException("用电排放计算需要1个参数：用电量(kWh)");
        }
        
        double electricityAmount = params[0];
        
        if (electricityAmount < 0) {
            throw new IllegalArgumentException("用电量不能为负数");
        }
        
        return ELECTRICITY_EMISSION_FACTOR * electricityAmount;
    }
    
    @Override
    public String getStrategyName() {
        return "用电排放计算";
    }
}
