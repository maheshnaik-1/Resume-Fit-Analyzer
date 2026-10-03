package com.resumeanalyzer.controller;

import com.resumeanalyzer.dto.DashboardStatsDto;
import com.resumeanalyzer.service.DashboardStatsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DashboardController {

    private final DashboardStatsService dashboardStatsService;

    public DashboardController(DashboardStatsService dashboardStatsService) {
        this.dashboardStatsService = dashboardStatsService;
    }

    @GetMapping("/dashboard-stats/{email}")
    public ResponseEntity<DashboardStatsDto> getDashboardStats(@PathVariable("email") String email) {
        DashboardStatsDto stats = dashboardStatsService.getDashboardStats(email);
        return ResponseEntity.ok(stats);
    }
}
