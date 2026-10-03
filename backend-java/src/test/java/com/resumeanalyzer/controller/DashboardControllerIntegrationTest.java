package com.resumeanalyzer.controller;

import com.resumeanalyzer.entity.ResumeHistory;
import com.resumeanalyzer.repository.ResumeHistoryRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class DashboardControllerIntegrationTest {

    private static final String TEST_EMAIL = "dashboard_test_user@example.com";

    @BeforeAll
    static void ensureTestDatabaseExists() throws IOException {
        Path targetDb = Path.of("target", "migration_test.db");
        Path sourceDb = Path.of("..", "backend", "resume_analyzer.db");
        if (!Files.exists(targetDb)) {
            Files.createDirectories(targetDb.getParent());
            if (Files.exists(sourceDb)) {
                Files.copy(sourceDb, targetDb, StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ResumeHistoryRepository resumeHistoryRepository;

    @BeforeEach
    void setUp() {
        cleanTestData();
    }

    @AfterEach
    void tearDown() {
        cleanTestData();
    }

    private void cleanTestData() {
        List<ResumeHistory> histories = resumeHistoryRepository.findByEmail(TEST_EMAIL);
        if (!histories.isEmpty()) {
            resumeHistoryRepository.deleteAll(histories);
        }
    }

    @Test
    @DisplayName("GET /dashboard-stats/{email} - Should return populated dashboard statistics")
    void testDashboardStatsPopulated() throws Exception {
        ResumeHistory h1 = new ResumeHistory(TEST_EMAIL, "Google", "Java Developer", 80.0, "2026-10-01 10:00:00", "{}");
        ResumeHistory h2 = new ResumeHistory(TEST_EMAIL, "google", "Backend Engineer", 90.0, "2026-10-02 11:00:00", "{}");
        ResumeHistory h3 = new ResumeHistory(TEST_EMAIL, "Amazon", "Cloud Engineer", 85.0, "2026-10-03 12:00:00", "{}");
        resumeHistoryRepository.saveAll(List.of(h1, h2, h3));

        mockMvc.perform(get("/dashboard-stats/" + TEST_EMAIL))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.total_analyses", is(3)))
                .andExpect(jsonPath("$.highest_score", is(90.0)))
                .andExpect(jsonPath("$.average_score", is(85.0)))
                .andExpect(jsonPath("$.companies", is(2)));
    }

    @Test
    @DisplayName("GET /dashboard-stats/{email} - Should return all zeros when user has no history")
    void testDashboardStatsEmpty() throws Exception {
        mockMvc.perform(get("/dashboard-stats/nonexistent_dashboard_user@example.com"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.total_analyses", is(0)))
                .andExpect(jsonPath("$.highest_score", is(0.0)))
                .andExpect(jsonPath("$.average_score", is(0.0)))
                .andExpect(jsonPath("$.companies", is(0)));
    }

    @Test
    @DisplayName("GET /dashboard-stats/{email} - Should normalize email case and whitespace")
    void testDashboardStatsNormalizedEmail() throws Exception {
        ResumeHistory h1 = new ResumeHistory(TEST_EMAIL, "Netflix", "Platform Engineer", 95.5, "2026-10-01 10:00:00", "{}");
        resumeHistoryRepository.save(h1);

        mockMvc.perform(get("/dashboard-stats/  DASHBOARD_TEST_USER@EXAMPLE.COM  "))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total_analyses", is(1)))
                .andExpect(jsonPath("$.highest_score", is(95.5)))
                .andExpect(jsonPath("$.average_score", is(95.5)))
                .andExpect(jsonPath("$.companies", is(1)));
    }
}
