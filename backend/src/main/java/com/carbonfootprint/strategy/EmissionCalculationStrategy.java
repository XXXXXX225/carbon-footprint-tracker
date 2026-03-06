package com.carbonfootprint.strategy;

public interface EmissionCalculationStrategy {
    double calculate(double... params);
    String getStrategyName();
}
