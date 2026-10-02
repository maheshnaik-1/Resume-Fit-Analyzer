package com.resumeanalyzer.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ResumeHealthServiceTest {

    private ResumeHealthService healthService;

    @BeforeEach
    void setUp() {
        healthService = new ResumeHealthService();
    }

    @Test
    @DisplayName("1. Perfect score produces all green indicators")
    void testAllGreenHealth() {
        List<String> health = healthService.calculateResumeHealth(100, 100, 100, 100);
        assertEquals(4, health.size());
        assertEquals("🟢 Strong technical skills", health.get(0));
        assertEquals("🟢 Excellent project portfolio", health.get(1));
        assertEquals("🟢 Education section looks complete", health.get(2));
        assertEquals("🟢 Certifications strengthen your profile", health.get(3));
    }

    @Test
    @DisplayName("2. Medium score produces yellow indicators")
    void testYellowHealth() {
        List<String> health = healthService.calculateResumeHealth(50, 50, 0, 50);
        assertEquals(4, health.size());
        assertEquals("🟡 Skills section can be improved", health.get(0));
        assertEquals("🟡 Add one stronger project", health.get(1));
        assertEquals("🟡 Improve education details", health.get(2));
        assertEquals("🟡 Add more certifications", health.get(3));
    }

    @Test
    @DisplayName("3. Zero score produces red/yellow indicators")
    void testRedHealth() {
        List<String> health = healthService.calculateResumeHealth(0, 0, 0, 0);
        assertEquals(4, health.size());
        assertEquals("🔴 Add more relevant technical skills", health.get(0));
        assertEquals("🔴 Projects section is weak", health.get(1));
        assertEquals("🟡 Improve education details", health.get(2));
        assertEquals("🔴 Certifications are missing", health.get(3));
    }

    @Test
    @DisplayName("4. Threshold boundary tests: 80, 50, 49")
    void testThresholdBoundaries() {
        // Exactly 80
        List<String> h80 = healthService.calculateResumeHealth(80, 80, 80, 80);
        assertEquals("🟢 Strong technical skills", h80.get(0));
        assertEquals("🟢 Excellent project portfolio", h80.get(1));
        assertEquals("🟢 Education section looks complete", h80.get(2));
        assertEquals("🟢 Certifications strengthen your profile", h80.get(3));

        // Exactly 79
        List<String> h79 = healthService.calculateResumeHealth(79, 79, 79, 79);
        assertEquals("🟡 Skills section can be improved", h79.get(0));
        assertEquals("🟡 Add one stronger project", h79.get(1));
        assertEquals("🟡 Improve education details", h79.get(2));
        assertEquals("🟡 Add more certifications", h79.get(3));

        // Exactly 49
        List<String> h49 = healthService.calculateResumeHealth(49, 49, 0, 49);
        assertEquals("🔴 Add more relevant technical skills", h49.get(0));
        assertEquals("🔴 Projects section is weak", h49.get(1));
        assertEquals("🟡 Improve education details", h49.get(2));
        assertEquals("🔴 Certifications are missing", h49.get(3));
    }
}
