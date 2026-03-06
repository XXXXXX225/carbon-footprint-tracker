package com.carbonfootprint.strategy.impl;

import com.carbonfootprint.strategy.EmissionCalculationStrategy;
import org.springframework.stereotype.Component;

@Component
public class DietEmissionStrategy implements EmissionCalculationStrategy {
    
    private static final double BEEF_EMISSION_FACTOR = 27.0;
    private static final double PORK_EMISSION_FACTOR = 6.0;
    private static final double CHICKEN_EMISSION_FACTOR = 4.5;
    private static final double VEGETABLE_EMISSION_FACTOR = 0.4;
    
    @Override
    public double calculate(double... params) {
        if (params.length < 2) {
            throw new IllegalArgumentException("饮食排放计算需要2个参数：食物类型和重量");
        }
        
        int foodType = (int) params[0];
        double weight = params[1];
        
        if (weight < 0) {
            throw new IllegalArgumentException("重量不能为负数");
        }
        
        return switch (foodType) {
            case 1 -> BEEF_EMISSION_FACTOR * weight;
            case 2 -> PORK_EMISSION_FACTOR * weight;
            case 3 -> CHICKEN_EMISSION_FACTOR * weight;
            case 4 -> VEGETABLE_EMISSION_FACTOR * weight;
            default -> throw new IllegalArgumentException("无效的食物类型：1-牛肉, 2-猪肉, 3-鸡肉, 4-蔬菜");
        };
    }
    
    @Override
    public String getStrategyName() {
        return "饮食排放计算";
    }
}
