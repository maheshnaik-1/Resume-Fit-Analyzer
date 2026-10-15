package com.resumeanalyzer.controller;

import com.resumeanalyzer.dto.DashboardStatsDto;
import com.resumeanalyzer.service.DashboardStatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Dashboard", description = "Endpoints for aggregated user dashboard metrics and analytics")
public class DashboardController {

    private final DashboardStatsService dashboardStatsService;

    public DashboardController(DashboardStatsService dashboardStatsService) {
        this.dashboardStatsService = dashboardStatsService;
    }

    @GetMapping("/dashboard-stats/{email}")
    @Operation(summary = "Get dashboard statistics", description = "Computes aggregated ATS scores, best resume match, analysis count, and improvements for the user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dashboard statistics returned successfully")
    })
    public ResponseEntity<DashboardStatsDto> getDashboardStats(
            @Parameter(description = "User account email address", required = true)
            @PathVariable("email") String email
    ) {
        DashboardStatsDto stats = dashboardStatsService.getDashboardStats(email);
        return ResponseEntity.ok(stats);
    }
}

