package com.carbonfootprint.config;

import com.carbonfootprint.entity.CarbonPredictionHistory;
import com.carbonfootprint.entity.FootprintSummary;
import com.carbonfootprint.entity.User;
import com.carbonfootprint.repository.CarbonPredictionHistoryRepository;
import com.carbonfootprint.repository.UserRepository;
import com.carbonfootprint.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;
import org.springframework.core.annotation.Order;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Component
@Order(3)
@RequiredArgsConstructor
@Slf4j
public class PredictionHistoryInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CarbonPredictionHistoryRepository predictionHistoryRepository;
    private final ReportService reportService;

    @Override
    @Transactional
    public void run(String... args) {
        if (predictionHistoryRepository.count() > 0) {
            return;
        }

        User user = userRepository.findByUsername("testuser")
                .orElseGet(() -> userRepository.findAll().stream().findFirst().orElse(null));

        if (user == null) {
            log.info("未找到可用于初始化预测历史的用户");
            return;
        }

        createSampleHistory(user.getId());
        log.info("预测历史初始化完成");
    }

    private void createSampleHistory(Long userId) {
        List<Double> offsets = List.of(-0.08, 0.05, -0.03);

        for (int i = 3; i >= 1; i--) {
            YearMonth targetMonth = YearMonth.now().minusMonths(i);
            FootprintSummary actualSummary = reportService.generateFootprintSummary(
                    userId,
                    FootprintSummary.Period.MONTHLY,
                    targetMonth.atEndOfMonth());

            double actualEmission = actualSummary.getTotalEmission();
            double predictedEmission = round(actualEmission * (1 + offsets.get(3 - i)));

            CarbonPredictionHistory history = new CarbonPredictionHistory();
            history.setUserId(userId);
            history.setTargetMonth(targetMonth.toString());
            history.setPredictionDate(targetMonth.minusMonths(1).atEndOfMonth());
            history.setPredictedEmission(predictedEmission);
            history.setConfidence(0.82 - (0.03 * (3 - i)));
            history.setTrend(i == 3 ? "保持稳定" : (i == 2 ? "下降趋势" : "上升趋势"));
            history.setActualEmission(actualEmission);
            history.setAbsoluteError(round(Math.abs(predictedEmission - actualEmission)));
            history.setErrorRate(actualEmission == 0 ? 0.0 : round(Math.abs(predictedEmission - actualEmission) / actualEmission * 100));
            history.setStatus("COMPLETED");

            predictionHistoryRepository.save(history);
        }
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}