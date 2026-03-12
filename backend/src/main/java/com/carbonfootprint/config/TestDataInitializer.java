package com.carbonfootprint.config;

import com.carbonfootprint.entity.*;
import com.carbonfootprint.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.Random;

@Component
@RequiredArgsConstructor
@Slf4j
public class TestDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final TransportEmissionRepository transportEmissionRepository;
    private final DietEmissionRepository dietEmissionRepository;
    private final ElectricityEmissionRepository electricityEmissionRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (shouldCreateTestData()) {
            createTestDataForUser(1L);
            log.info("测试数据创建完成");
        }
    }

    private boolean shouldCreateTestData() {
        long transportCount = transportEmissionRepository.count();
        long dietCount = dietEmissionRepository.count();
        long electricityCount = electricityEmissionRepository.count();
        
        log.info("当前数据统计 - 交通记录: {}, 饮食记录: {}, 用电记录: {}", transportCount, dietCount, electricityCount);
        
        return transportCount == 0 && dietCount == 0 && electricityCount == 0;
    }

    private void createTestDataForUser(Long userId) {
        Random random = new Random();
        LocalDate today = LocalDate.now();
        
        for (int i = 0; i < 90; i++) {
            LocalDate date = today.minusDays(i);
            
            createTransportEmission(userId, date, random);
            createDietEmission(userId, date, random);
            createElectricityEmission(userId, date, random);
        }
    }

    private void createTransportEmission(Long userId, LocalDate date, Random random) {
        if (random.nextDouble() > 0.3) {
            TransportEmission emission = new TransportEmission();
            emission.setUserId(userId);
            emission.setTransportType(random.nextInt(6) + 1);
            emission.setDistance(5.0 + random.nextDouble() * 30.0);
            emission.setEmissionAmount(emission.getDistance() * 0.15 * (random.nextDouble() * 0.5 + 0.75));
            emission.setEmissionDate(date);
            emission.setDescription("日常出行");
            transportEmissionRepository.save(emission);
        }
    }

    private void createDietEmission(Long userId, LocalDate date, Random random) {
        if (random.nextDouble() > 0.4) {
            DietEmission emission = new DietEmission();
            emission.setUserId(userId);
            emission.setFoodType(random.nextInt(5) + 1);
            emission.setSpecificFood("测试食物");
            emission.setAmount(1.0 + random.nextDouble() * 2.0);
            emission.setEmissionAmount(emission.getAmount() * 2.5 * (random.nextDouble() * 0.4 + 0.8));
            emission.setEmissionDate(date);
            emission.setDescription("日常饮食");
            dietEmissionRepository.save(emission);
        }
    }

    private void createElectricityEmission(Long userId, LocalDate date, Random random) {
        if (random.nextDouble() > 0.2) {
            ElectricityEmission emission = new ElectricityEmission();
            emission.setUserId(userId);
            emission.setDeviceType("测试设备");
            emission.setPower(500.0 + random.nextDouble() * 1000.0);
            emission.setUsageTime(1.0 + random.nextDouble() * 8.0);
            emission.setUsageDays(1);
            emission.setElectricityAmount(emission.getPower() * emission.getUsageTime() / 1000.0);
            emission.setEmissionAmount(emission.getElectricityAmount() * 0.5 * (random.nextDouble() * 0.3 + 0.85));
            emission.setEmissionDate(date);
            emission.setDescription("日常用电");
            electricityEmissionRepository.save(emission);
        }
    }
}