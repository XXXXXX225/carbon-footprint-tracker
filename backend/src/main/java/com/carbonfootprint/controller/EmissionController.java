package com.carbonfootprint.controller;

import com.carbonfootprint.dto.ApiResult;
import com.carbonfootprint.dto.DietEmissionDTO;
import com.carbonfootprint.dto.ElectricityEmissionDTO;
import com.carbonfootprint.dto.EmissionsSummaryDTO;
import com.carbonfootprint.dto.TransportEmissionDTO;
import com.carbonfootprint.entity.DietEmission;
import com.carbonfootprint.entity.ElectricityEmission;
import com.carbonfootprint.entity.TransportEmission;
import com.carbonfootprint.service.EmissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/emission")
@RequiredArgsConstructor
@Tag(name = "排放记录", description = "碳排放记录相关接口")
public class EmissionController {

    private final EmissionService emissionService;

    /**
     * 记录交通排放
     * 
     * @param authentication 认证信息
     * @param dto            交通排放DTO
     * @return 交通排放记录
     */
    @PostMapping("/transport")
    @Operation(summary = "记录交通排放", description = "记录用户的交通活动碳排放")
    public ApiResult<TransportEmissionDTO> recordTransport(
            Authentication authentication,
            @RequestBody TransportEmissionDTO dto) {
        Long userId = getUserIdFromAuthentication(authentication);
        TransportEmission emission = emissionService.recordTransportEmission(userId, dto);
        TransportEmissionDTO result = new TransportEmissionDTO();
        result.setTransportType(Integer.parseInt(emission.getTransportType()));
        result.setDistance(emission.getDistance());
        result.setEmissionDate(emission.getEmissionDate());
        result.setDescription(emission.getDescription());
        return ApiResult.success(result);
    }

    /**
     * 记录饮食排放
     * 
     * @param authentication 认证信息
     * @param dto            饮食排放DTO
     * @return 饮食排放记录
     */
    @PostMapping("/diet")
    @Operation(summary = "记录饮食排放", description = "记录用户的饮食活动碳排放")
    public ApiResult<DietEmissionDTO> recordDietEmission(
            Authentication authentication,
            @RequestBody DietEmissionDTO dto) {
        Long userId = getUserIdFromAuthentication(authentication);
        DietEmission emission = emissionService.recordDietEmission(userId, dto);
        DietEmissionDTO result = new DietEmissionDTO();
        result.setFoodType(Integer.parseInt(emission.getFoodType()));
        result.setSpecificFood(emission.getSpecificFood());
        result.setAmount(emission.getAmount());
        result.setEmissionDate(emission.getEmissionDate());
        result.setDescription(emission.getDescription());
        return ApiResult.success(result);
    }

    /**
     * 记录用电排放
     * 
     * @param authentication 认证信息
     * @param dto            用电排放DTO
     * @return 用电排放记录
     */
    @PostMapping("/electricity")
    @Operation(summary = "记录用电排放", description = "记录用户的用电活动碳排放")
    public ApiResult<ElectricityEmissionDTO> recordElectricityEmission(
            Authentication authentication,
            @RequestBody ElectricityEmissionDTO dto) {
        Long userId = getUserIdFromAuthentication(authentication);
        ElectricityEmission emission = emissionService.recordElectricityEmission(userId, dto);
        ElectricityEmissionDTO result = new ElectricityEmissionDTO();
        result.setDeviceType(emission.getDeviceType());
        result.setPower(emission.getPower());
        result.setUsageTime(emission.getUsageTime());
        result.setUsageDays(emission.getUsageDays());
        result.setEmissionDate(emission.getEmissionDate());
        result.setDescription(emission.getDescription());
        return ApiResult.success(result);
    }

    /**
     * 获取用户的交通排放记录
     * 
     * @param authentication 认证信息
     * @return 交通排放记录列表
     */
    @GetMapping("/transport")
    @Operation(summary = "获取交通排放记录", description = "获取用户的交通排放记录列表")
    public ApiResult<List<TransportEmission>> getTransportEmissions(Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        List<TransportEmission> emissions = emissionService.getTransportEmissions(userId);
        return ApiResult.success(emissions);
    }

    /**
     * 获取用户的饮食排放记录
     * 
     * @param authentication 认证信息
     * @return 饮食排放记录列表
     */
    @GetMapping("/diet")
    @Operation(summary = "获取饮食排放记录", description = "获取用户的饮食排放记录列表")
    public ApiResult<List<DietEmission>> getDietEmissions(Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        List<DietEmission> emissions = emissionService.getDietEmissions(userId);
        return ApiResult.success(emissions);
    }

    /**
     * 获取用户的用电排放记录
     * 
     * @param authentication 认证信息
     * @return 用电排放记录列表
     */
    @GetMapping("/electricity")
    @Operation(summary = "获取用电排放记录", description = "获取用户的用电排放记录列表")
    public ApiResult<List<ElectricityEmission>> getElectricityEmissions(Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        List<ElectricityEmission> emissions = emissionService.getElectricityEmissions(userId);
        return ApiResult.success(emissions);
    }

    @DeleteMapping("/transport/{id}")
    @Operation(summary = "删除交通排放记录", description = "删除指定的交通排放记录")
    public ApiResult<Void> deleteTransportEmission(
            Authentication authentication,
            @PathVariable Long id) {
        Long userId = getUserIdFromAuthentication(authentication);
        emissionService.deleteTransportEmission(userId, id);
        return ApiResult.success();
    }

    @DeleteMapping("/transport")
    @Operation(summary = "清空交通排放记录", description = "清空当前用户的所有交通排放记录")
    public ApiResult<Void> clearTransportEmissions(Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        emissionService.clearTransportEmissions(userId);
        return ApiResult.success();
    }

    @DeleteMapping("/diet/{id}")
    @Operation(summary = "删除饮食排放记录", description = "删除指定的饮食排放记录")
    public ApiResult<Void> deleteDietEmission(
            Authentication authentication,
            @PathVariable Long id) {
        Long userId = getUserIdFromAuthentication(authentication);
        emissionService.deleteDietEmission(userId, id);
        return ApiResult.success();
    }

    @DeleteMapping("/diet")
    @Operation(summary = "清空饮食排放记录", description = "清空当前用户的所有饮食排放记录")
    public ApiResult<Void> clearDietEmissions(Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        emissionService.clearDietEmissions(userId);
        return ApiResult.success();
    }

    @DeleteMapping("/electricity/{id}")
    @Operation(summary = "删除用电排放记录", description = "删除指定的用电排放记录")
    public ApiResult<Void> deleteElectricityEmission(
            Authentication authentication,
            @PathVariable Long id) {
        Long userId = getUserIdFromAuthentication(authentication);
        emissionService.deleteElectricityEmission(userId, id);
        return ApiResult.success();
    }

    @DeleteMapping("/electricity")
    @Operation(summary = "清空用电排放记录", description = "清空当前用户的所有用电排放记录")
    public ApiResult<Void> clearElectricityEmissions(Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        emissionService.clearElectricityEmissions(userId);
        return ApiResult.success();
    }

    /**
     * 获取用户的交通排放汇总
     * 
     * @param authentication 认证信息
     * @param period         汇总周期（week/month/year）
     * @return 交通排放汇总
     */
    @GetMapping("/transport/summary")
    @Operation(summary = "获取交通排放汇总", description = "按周/月/年汇总用户的交通排放")
    public ApiResult<EmissionsSummaryDTO> getTransportSummary(
            Authentication authentication,
            @RequestParam String period) {
        Long userId = getUserIdFromAuthentication(authentication);
        EmissionsSummaryDTO summary = emissionService.getTransportEmissionSummary(userId, period);
        return ApiResult.success(summary);
    }

    /**
     * 获取用户的总排放汇总
     * 
     * @param authentication 认证信息
     * @param period         汇总周期（week/month/year）
     * @return 总排放汇总
     */
    @GetMapping("/summary")
    @Operation(summary = "获取总排放汇总", description = "按周/月/年汇总用户的总排放")
    public ApiResult<EmissionsSummaryDTO> getEmissionsSummary(
            Authentication authentication,
            @RequestParam String period) {
        Long userId = getUserIdFromAuthentication(authentication);
        EmissionsSummaryDTO summary = emissionService.getEmissionsSummary(userId, period);
        return ApiResult.success(summary);
    }

    /**
     * 从认证信息中获取用户ID
     * 
     * @param authentication 认证信息
     * @return 用户ID
     */
    private Long getUserIdFromAuthentication(Authentication authentication) {
        com.carbonfootprint.entity.User user = (com.carbonfootprint.entity.User) authentication.getPrincipal();
        return user.getId();
    }
}
