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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "数据大屏", description = "数据可视化大屏接口")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/data")
    @Operation(summary = "获取大屏数据", description = "获取数据可视化大屏所需的所有数据")
    public ResponseEntity<ApiResult<DashboardDataDTO>> getDashboardData() {
        DashboardDataDTO data = dashboardService.getDashboardData();
        return ResponseEntity.ok(ApiResult.success(data, "获取成功"));
    }
}
