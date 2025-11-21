package com.pradip.sewearn.controller;

import com.pradip.sewearn.dto.ApiResponse;
import com.pradip.sewearn.dto.DashboardSummaryResponse;
import com.pradip.sewearn.dto.WeeklyMaterialPieResponse;
import com.pradip.sewearn.dto.WeeklyProgressResponse;
import com.pradip.sewearn.service.DashboardService;
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
@Tag(name = "Dashboard", description = "Dashboard analytics summary")
public class DashboardController {

    private final DashboardService dashboardService;

    @Operation(summary = "Get dashboard summary",
            description = "Returns monthly earnings, today's earnings, today's completed quantity, and total pending work.")
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<DashboardSummaryResponse>> getSummary() {

        DashboardSummaryResponse response = dashboardService.getDashboardSummary();

        return ResponseEntity.ok(ApiResponse.success(
                "Dashboard summary fetched successfully",
                response
        ));
    }

    @Operation(
            summary = "Weekly earnings graph data",
            description = "Returns Mon–Sun earnings, week totals, previous week totals, % difference."
    )
    @GetMapping("/weekly-progress")
    public ResponseEntity<ApiResponse<WeeklyProgressResponse>> getWeeklyProgress(
            @RequestParam(defaultValue = "0") int week
    ) {
        WeeklyProgressResponse response = dashboardService.getWeeklyProgress(week);
        return ResponseEntity.ok(ApiResponse.success("Weekly progress fetched", response));
    }

    @Operation(
            summary = "Material-wise weekly earnings (Pie Chart)",
            description = "Returns earnings grouped by raw material type for a selected week."
    )
    @GetMapping("/weekly-material-pie")
    public ResponseEntity<ApiResponse<WeeklyMaterialPieResponse>> getWeeklyMaterialPie(
            @RequestParam(defaultValue = "0") int week
    ) {
        WeeklyMaterialPieResponse response = dashboardService.getWeeklyMaterialWisePie(week);
        return ResponseEntity.ok(ApiResponse.success("Material-wise earnings fetched", response));
    }
}