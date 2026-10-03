package com.resumeanalyzer.service;

import com.resumeanalyzer.dto.DashboardStatsDto;
import com.resumeanalyzer.entity.ResumeHistory;
import com.resumeanalyzer.repository.ResumeHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DashboardStatsServiceTest {

    @Mock
    private ResumeHistoryRepository resumeHistoryRepository;

    @InjectMocks
    private DashboardStatsService dashboardStatsService;

    @Test
    @DisplayName("Should return 0 for all stats when email is null")
    void testNullEmailReturnsZeros() {
        DashboardStatsDto stats = dashboardStatsService.getDashboardStats(null);
        assertNotNull(stats);
        assertEquals(0, stats.getTotalAnalyses());
        assertEquals(0.0, stats.getHighestScore());
        assertEquals(0.0, stats.getAverageScore());
        assertEquals(0, stats.getCompanies());
        verifyNoInteractions(resumeHistoryRepository);
    }

    @Test
    @DisplayName("Should return 0 for all stats when user has no history")
    void testEmptyHistoryReturnsZeros() {
        when(resumeHistoryRepository.findByEmail("user@example.com")).thenReturn(Collections.emptyList());

        DashboardStatsDto stats = dashboardStatsService.getDashboardStats("user@example.com");
        assertNotNull(stats);
        assertEquals(0, stats.getTotalAnalyses());
        assertEquals(0.0, stats.getHighestScore());
        assertEquals(0.0, stats.getAverageScore());
        assertEquals(0, stats.getCompanies());
        verify(resumeHistoryRepository, times(1)).findByEmail("user@example.com");
    }

    @Test
    @DisplayName("Should calculate statistics correctly for populated history")
    void testPopulatedHistoryCalculations() {
        List<ResumeHistory> history = List.of(
                new ResumeHistory("user@example.com", "Google", "Software Engineer", 85.5, "2026-10-01 10:00:00", "{}"),
                new ResumeHistory("user@example.com", "Amazon", "Backend Developer", 92.0, "2026-10-02 11:00:00", "{}"),
                new ResumeHistory("user@example.com", "Microsoft", "Full Stack Developer", 78.5, "2026-10-03 12:00:00", "{}")
        );
        when(resumeHistoryRepository.findByEmail("user@example.com")).thenReturn(history);

        DashboardStatsDto stats = dashboardStatsService.getDashboardStats("  USER@Example.com ");
        assertNotNull(stats);
        assertEquals(3, stats.getTotalAnalyses());
        assertEquals(92.0, stats.getHighestScore());
        // (85.5 + 92.0 + 78.5) / 3 = 256.0 / 3 = 85.33333333333333 -> 85.33
        assertEquals(85.33, stats.getAverageScore());
        assertEquals(3, stats.getCompanies());
    }

    @Test
    @DisplayName("Should deduplicate companies case-insensitively and ignore empty/whitespace/null company names")
    void testDistinctCompaniesFiltering() {
        List<ResumeHistory> history = List.of(
                new ResumeHistory("user@example.com", "Google", "Role 1", 80.0, "2026-10-01", "{}"),
                new ResumeHistory("user@example.com", " google ", "Role 2", 82.0, "2026-10-02", "{}"),
                new ResumeHistory("user@example.com", "GOOGLE", "Role 3", 84.0, "2026-10-03", "{}"),
                new ResumeHistory("user@example.com", "Microsoft", "Role 4", 86.0, "2026-10-04", "{}"),
                new ResumeHistory("user@example.com", "   ", "Role 5", 88.0, "2026-10-05", "{}"),
                new ResumeHistory("user@example.com", "", "Role 6", 90.0, "2026-10-06", "{}"),
                new ResumeHistory("user@example.com", null, "Role 7", 92.0, "2026-10-07", "{}")
        );
        when(resumeHistoryRepository.findByEmail("user@example.com")).thenReturn(history);

        DashboardStatsDto stats = dashboardStatsService.getDashboardStats("user@example.com");
        assertNotNull(stats);
        assertEquals(7, stats.getTotalAnalyses());
        assertEquals(92.0, stats.getHighestScore());
        // (80+82+84+86+88+90+92)/7 = 602/7 = 86.0
        assertEquals(86.0, stats.getAverageScore());
        // Only "google" and "microsoft"
        assertEquals(2, stats.getCompanies());
    }

    @Test
    @DisplayName("Should apply HALF_EVEN rounding to 2 decimal places matching Python round()")
    void testHalfEvenRounding() {
        // 75.125 with HALF_EVEN rounds to 75.12 (2 is even)
        // 75.135 with HALF_EVEN rounds to 75.14 (4 is even)
        List<ResumeHistory> history = List.of(
                new ResumeHistory("user@example.com", "Meta", "Role 1", 75.125, "2026-10-01", "{}")
        );
        when(resumeHistoryRepository.findByEmail("user@example.com")).thenReturn(history);

        DashboardStatsDto stats = dashboardStatsService.getDashboardStats("user@example.com");
        assertEquals(75.12, stats.getHighestScore());
        assertEquals(75.12, stats.getAverageScore());
    }
}
