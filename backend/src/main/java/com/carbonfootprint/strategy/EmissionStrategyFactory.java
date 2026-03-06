package com.carbonfootprint.strategy;

import com.carbonfootprint.strategy.impl.DietEmissionStrategy;
import com.carbonfootprint.strategy.impl.ElectricityEmissionStrategy;
import com.carbonfootprint.strategy.impl.TransportEmissionStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class EmissionStrategyFactory {
    
    private final Map<String, EmissionCalculationStrategy> strategyMap;
    
    @Autowired
    public EmissionStrategyFactory(TransportEmissionStrategy transportEmissionStrategy,
                                  DietEmissionStrategy dietEmissionStrategy,
                                  ElectricityEmissionStrategy electricityEmissionStrategy) {
        this.strategyMap = new HashMap<>();
        strategyMap.put("transport", transportEmissionStrategy);
        strategyMap.put("diet", dietEmissionStrategy);
        strategyMap.put("electricity", electricityEmissionStrategy);
    }
    
    public EmissionCalculationStrategy getStrategy(String strategyType) {
        EmissionCalculationStrategy strategy = strategyMap.get(strategyType.toLowerCase());
        if (strategy == null) {
            throw new IllegalArgumentException("未知的排放计算策略类型: " + strategyType);
        }
        return strategy;
    }
}
