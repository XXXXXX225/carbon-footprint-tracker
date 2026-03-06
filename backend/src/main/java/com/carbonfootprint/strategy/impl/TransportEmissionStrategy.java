package com.carbonfootprint.strategy.impl;

import com.carbonfootprint.strategy.EmissionCalculationStrategy;
import org.springframework.stereotype.Component;

@Component
public class TransportEmissionStrategy implements EmissionCalculationStrategy {
    
    private static final double CAR_EMISSION_FACTOR = 0.21;
    private static final double BUS_EMISSION_FACTOR = 0.089;
    private static final double SUBWAY_EMISSION_FACTOR = 0.046;
    private static final double PLANE_EMISSION_FACTOR = 0.179;
    
    @Override
    public double calculate(double... params) {
        if (params.length < 2) {
            throw new IllegalArgumentException("交通排放计算需要2个参数：交通工具类型和距离");
        }
        
        int transportType = (int) params[0];
        double distance = params[1];
        
        if (distance < 0) {
            throw new IllegalArgumentException("距离不能为负数");
        }
        
        return switch (transportType) {
            case 1 -> CAR_EMISSION_FACTOR * distance;
            case 2 -> BUS_EMISSION_FACTOR * distance;
            case 3 -> SUBWAY_EMISSION_FACTOR * distance;
            case 4 -> PLANE_EMISSION_FACTOR * distance;
            default -> throw new IllegalArgumentException("无效的交通工具类型：1-小汽车, 2-公交车, 3-地铁, 4-飞机");
        };
    }
    
    @Override
    public String getStrategyName() {
        return "交通排放计算";
    }
}
