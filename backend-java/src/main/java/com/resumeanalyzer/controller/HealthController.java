package com.resumeanalyzer.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Tag(name = "Health Check", description = "Backend service health status check")
public class HealthController {

    @GetMapping("/")
    @Operation(summary = "Root health check", description = "Returns service health status, application name, and version.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Service is healthy")
    })
    public ResponseEntity<Map<String, String>> rootHealthCheck() {
        return ResponseEntity.ok(Map.of(
                "message", "Resume Analyzer Java Backend Working Successfully",
                "status", "healthy",
                "version", "1.5.0-SNAPSHOT"
        ));
    }
}

