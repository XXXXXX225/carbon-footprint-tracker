package com.carbonfootprint.service;

import com.carbonfootprint.dto.CarbonPredictionDTO;
import com.carbonfootprint.entity.DietEmission;
import com.carbonfootprint.entity.ElectricityEmission;
import com.carbonfootprint.entity.TransportEmission;
import com.carbonfootprint.repository.DietEmissionRepository;
import com.carbonfootprint.repository.ElectricityEmissionRepository;
import com.carbonfootprint.repository.TransportEmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarbonPredictionService {

    private final TransportEmissionRepository transportEmissionRepository;
    private final DietEmissionRepository dietEmissionRepository;
    private final ElectricityEmissionRepository electricityEmissionRepository;

    public CarbonPredictionDTO predictNextMonth(Long userId) {
        System.out.println("开始预测，用户ID: " + userId);
        
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusMonths(3);
        
        List<TransportEmission> transportEmissions = transportEmissionRepository
                .findByUserIdAndEmissionDateBetween(userId, startDate, today);
        List<DietEmission> dietEmissions = dietEmissionRepository
                .findByUserIdAndEmissionDateBetween(userId, startDate, today);
        List<ElectricityEmission> electricityEmissions = electricityEmissionRepository
                .findByUserIdAndEmissionDateBetween(userId, startDate, today);
        
        System.out.println("交通排放记录数: " + transportEmissions.size());
        System.out.println("饮食排放记录数: " + dietEmissions.size());
        System.out.println("用电排放记录数: " + electricityEmissions.size());
        
        Map<LocalDate, Double> dailyEmissions = aggregateDailyEmissions(
                transportEmissions, dietEmissions, electricityEmissions);
        
        System.out.println("有排放记录的天数: " + dailyEmissions.size());
        
        List<Double> historicalData = getHistoricalSequence(dailyEmissions, startDate, today);
        
        System.out.println("历史数据点数: " + historicalData.size());
        System.out.println("历史数据总和: " + historicalData.stream().mapToDouble(Double::doubleValue).sum());
        
        // Handle case with no data
        boolean hasNoData = historicalData.stream().allMatch(d -> d == 0.0);
        double predictedEmission;
        double confidence;
        String trend;
        
        if (hasNoData) {
            predictedEmission = 0.0;
            confidence = 0.0;
            trend = "暂无足够的历史数据，请先记录您的碳排放活动";
        } else {
            predictedEmission = linearRegressionPredict(historicalData);
            confidence = calculateConfidence(historicalData);
            trend = determineTrend(historicalData);
        }
        
        System.out.println("预测排放: " + predictedEmission);
        System.out.println("置信度: " + confidence);
        System.out.println("趋势: " + trend);
        
        List<CarbonPredictionDTO.DailyPrediction> dailyPredictions = 
                generateDailyPredictions(predictedEmission, today);
        
        List<CarbonPredictionDTO.MonthlyPrediction> monthlyPredictions = 
                generateMonthlyPredictions(historicalData);
        
        List<CarbonPredictionDTO.ReductionSuggestion> suggestions = 
                generateSuggestions(transportEmissions, dietEmissions, electricityEmissions);
        
        CarbonPredictionDTO prediction = new CarbonPredictionDTO();
        prediction.setUserId(userId);
        prediction.setPredictionDate(today.plusMonths(1));
        prediction.setPredictedEmission(Math.round(predictedEmission * 100.0) / 100.0);
        prediction.setConfidence(Math.round(confidence * 100.0) / 100.0);
        prediction.setTrend(trend);
        prediction.setDailyPredictions(dailyPredictions);
        prediction.setMonthlyPredictions(monthlyPredictions);
        prediction.setSuggestion(suggestions.isEmpty() ? null : suggestions.get(0));
        
        System.out.println("预测结果: " + prediction);
        
        return prediction;
    }

    private Map<LocalDate, Double> aggregateDailyEmissions(
            List<TransportEmission> transport,
            List<DietEmission> diet,
            List<ElectricityEmission> electricity) {
        
        Map<LocalDate, Double> dailyMap = new HashMap<>();
        
        transport.forEach(e -> dailyMap.merge(e.getEmissionDate(), e.getEmissionAmount(), Double::sum));
        diet.forEach(e -> dailyMap.merge(e.getEmissionDate(), e.getEmissionAmount(), Double::sum));
        electricity.forEach(e -> dailyMap.merge(e.getEmissionDate(), e.getEmissionAmount(), Double::sum));
        
        return dailyMap;
    }

    private List<Double> getHistoricalSequence(Map<LocalDate, Double> dailyEmissions, 
                                               LocalDate startDate, LocalDate endDate) {
        List<Double> sequence = new ArrayList<>();
        LocalDate current = startDate;
        
        while (!current.isAfter(endDate)) {
            sequence.add(dailyEmissions.getOrDefault(current, 0.0));
            current = current.plusDays(1);
        }
        
        return sequence;
    }

    private double linearRegressionPredict(List<Double> data) {
        if (data.isEmpty()) return 0.0;
        
        int n = data.size();
        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;
        
        for (int i = 0; i < n; i++) {
            sumX += i;
            sumY += data.get(i);
            sumXY += i * data.get(i);
            sumX2 += i * i;
        }
        
        double slope = (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX);
        double intercept = (sumY - slope * sumX) / n;
        
        double prediction = slope * (n + 30) + intercept;
        
        return Math.max(0, prediction);
    }

    private double calculateConfidence(List<Double> data) {
        if (data.size() < 2) return 0.5;
        
        double mean = data.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double variance = data.stream()
                .mapToDouble(d -> Math.pow(d - mean, 2))
                .average().orElse(0.0);
        double stdDev = Math.sqrt(variance);
        
        double coefficient = stdDev / (mean + 0.001);
        double confidence = 1.0 / (1.0 + coefficient);
        
        return Math.min(0.95, Math.max(0.5, confidence));
    }

    private String determineTrend(List<Double> data) {
        if (data.size() < 7) return "数据不足";
        
        int n = data.size();
        double firstWeek = data.stream().limit(7).mapToDouble(Double::doubleValue).sum();
        double lastWeek = data.stream().skip(n - 7).mapToDouble(Double::doubleValue).sum();
        
        double changeRate = (lastWeek - firstWeek) / (firstWeek + 0.001);
        
        if (changeRate > 0.1) return "上升趋势";
        else if (changeRate < -0.1) return "下降趋势";
        else return "保持稳定";
    }

    private List<CarbonPredictionDTO.DailyPrediction> generateDailyPredictions(
            double monthlyPrediction, LocalDate startDate) {
        
        List<CarbonPredictionDTO.DailyPrediction> predictions = new ArrayList<>();
        double dailyAvg = monthlyPrediction / 30.0;
        Random random = new Random();
        
        for (int i = 1; i <= 30; i++) {
            LocalDate date = startDate.plusDays(i);
            double variation = (random.nextDouble() - 0.5) * 0.4;
            double predicted = dailyAvg * (1 + variation);
            
            predictions.add(new CarbonPredictionDTO.DailyPrediction(
                    date,
                    Math.round(predicted * 100.0) / 100.0,
                    Math.round(predicted * 0.8 * 100.0) / 100.0,
                    Math.round(predicted * 1.2 * 100.0) / 100.0
            ));
        }
        
        return predictions;
    }

    private List<CarbonPredictionDTO.MonthlyPrediction> generateMonthlyPredictions(
            List<Double> historicalData) {
        
        List<CarbonPredictionDTO.MonthlyPrediction> predictions = new ArrayList<>();
        
        int daysPerMonth = 30;
        int totalDays = historicalData.size();
        
        for (int i = 0; i < totalDays; i += daysPerMonth) {
            int end = Math.min(i + daysPerMonth, totalDays);
            double monthSum = historicalData.subList(i, end).stream()
                    .mapToDouble(Double::doubleValue)
                    .sum();
            
            YearMonth yearMonth = YearMonth.now().minusMonths((totalDays - i) / daysPerMonth);
            String monthStr = yearMonth.toString();
            
            predictions.add(new CarbonPredictionDTO.MonthlyPrediction(
                    monthStr,
                    Math.round(monthSum * 100.0) / 100.0,
                    0.0
            ));
        }
        
        if (predictions.size() >= 2) {
            for (int i = 1; i < predictions.size(); i++) {
                double prev = predictions.get(i - 1).getPredictedEmission();
                double curr = predictions.get(i).getPredictedEmission();
                double trend = prev > 0 ? ((curr - prev) / prev) * 100 : 0;
                predictions.get(i).setTrend(Math.round(trend * 100.0) / 100.0);
            }
        }
        
        return predictions;
    }

    private List<CarbonPredictionDTO.ReductionSuggestion> generateSuggestions(
            List<TransportEmission> transport,
            List<DietEmission> diet,
            List<ElectricityEmission> electricity) {
        
        List<CarbonPredictionDTO.ReductionSuggestion> suggestions = new ArrayList<>();
        
        if (!transport.isEmpty()) {
            Map<Integer, Double> transportByType = transport.stream()
                    .filter(e -> e.getTransportType() != null)
                    .collect(Collectors.groupingBy(
                            e -> { 
                                try { 
                                    return Integer.parseInt(e.getTransportType()); 
                                } catch (NumberFormatException ex) { 
                                    return 0; 
                                } 
                            },
                            Collectors.summingDouble(TransportEmission::getEmissionAmount)));
            
            Integer maxType = transportByType.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse(0);
            
            // 假设 1=步行, 2=骑行, 3=公共汽车, 4=地铁, 5=出租车, 6=私家车
            if (maxType > 2) {
                String typeName = "其他交通工具";
                switch (maxType) {
                    case 3: typeName = "公共汽车";
                        break;
                    case 4: typeName = "地铁";
                        break;
                    case 5: typeName = "出租车";
                        break;
                    case 6: typeName = "私家车";
                        break;
                }
                suggestions.add(new CarbonPredictionDTO.ReductionSuggestion(
                        "交通出行",
                        "建议增加步行或骑行，减少" + typeName + "的使用",
                        transportByType.getOrDefault(maxType, 0.0) * 0.3,
                        1
                ));
            }
        }
        
        if (!diet.isEmpty()) {
            Map<Integer, Double> dietByType = diet.stream()
                    .filter(e -> e.getFoodType() != null)
                    .collect(Collectors.groupingBy(
                            e -> { 
                                try { 
                                    return Integer.parseInt(e.getFoodType()); 
                                } catch (NumberFormatException ex) { 
                                    return 0; 
                                } 
                            },
                            Collectors.summingDouble(DietEmission::getEmissionAmount)));
            
            // 假设 1=牛肉, 2=羊肉, 3=鸡肉, 4=蔬菜, 5=水果
            if (dietByType.containsKey(1) || dietByType.containsKey(2)) {
                suggestions.add(new CarbonPredictionDTO.ReductionSuggestion(
                        "饮食习惯",
                        "建议减少红肉摄入，增加蔬菜水果比例",
                        dietByType.getOrDefault(1, 0.0) * 0.5,
                        2
                ));
            }
        }
        
        if (!electricity.isEmpty()) {
            double totalElectricity = electricity.stream()
                    .mapToDouble(ElectricityEmission::getEmissionAmount)
                    .sum();
            
            if (totalElectricity > 50) {
                suggestions.add(new CarbonPredictionDTO.ReductionSuggestion(
                        "用电节能",
                        "建议使用节能设备，关闭待机电器",
                        totalElectricity * 0.2,
                        3
                ));
            }
        }
        
        return suggestions.stream()
                .sorted(Comparator.comparing(CarbonPredictionDTO.ReductionSuggestion::getPriority))
                .collect(Collectors.toList());
    }
}
