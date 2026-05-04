package com.carbonfootprint.controller;

import com.carbonfootprint.dto.ApiResult;
import com.carbonfootprint.dto.DashboardDataDTO;
import com.carbonfootprint.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Dashboard API")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/data")
    @Operation(summary = "Get Dashboard Data", description = "Get Dashboard Data")
    public ResponseEntity<ApiResult<DashboardDataDTO>> getDashboardData(
            @RequestParam(required = false, defaultValue = "month") String range) {
        DashboardDataDTO data = dashboardService.getDashboardData(range);
        return ResponseEntity.ok(ApiResult.success(data, "Success"));
    }
}