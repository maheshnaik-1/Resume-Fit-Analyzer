package com.resumeanalyzer.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/")
    public ResponseEntity<Map<String, String>> rootHealthCheck() {
        return ResponseEntity.ok(Map.of(
                "message", "Resume Analyzer Java Backend Working Successfully",
                "status", "healthy",
                "version", "1.5.0-SNAPSHOT"
        ));
    }
}
