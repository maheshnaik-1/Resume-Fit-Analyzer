package com.resumeanalyzer.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.resumeanalyzer.entity.Notification;
import com.resumeanalyzer.entity.ResumeHistory;
import com.resumeanalyzer.repository.NotificationRepository;
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
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class HistoryControllerIntegrationTest {

    private static final String TEST_EMAIL = "history_test_user@example.com";

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

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private ObjectMapper objectMapper;

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
        List<Notification> notifs = notificationRepository.findAll().stream()
                .filter(n -> TEST_EMAIL.equalsIgnoreCase(n.getEmail()))
                .toList();
        if (!notifs.isEmpty()) {
            notificationRepository.deleteAll(notifs);
        }
    }

    private String sampleAnalysisResultJson() {
        return """
        {
            "filename": "resume.pdf",
            "company": "Google",
            "role": "Java Developer",
            "target_company": "Google",
            "target_role": "Java Developer",
            "resume_summary": {
                "education": ["B.Tech Computer Science"],
                "skills": ["Java", "Spring Boot", "SQL"],
                "projects": ["Resume Analyzer"],
                "certifications": ["AWS Certified Developer"]
            },
            "job_skills": ["Java", "Spring Boot", "SQL", "Docker"],
            "matched_skills": ["Java", "Spring Boot", "SQL"],
            "missing_skills": ["Docker"],
            "ats_score": 85.5,
            "score_breakdown": {
                "skills": 75,
                "projects": 20,
                "education": 10,
                "certifications": 5
            },
            "recommended_roles": ["Backend Developer", "Java Developer"],
            "top_matches": [
                {"role": "Java Developer", "matchScore": 85.5},
                {"role": "Backend Developer", "matchScore": 80.0}
            ],
            "analysis_time": 0.45,
            "resume_health": ["🟢 Strong technical skills"],
            "suggestions": ["Add Docker experience"],
            "best_resume": null,
            "resume_improvement": null
        }
        """;
    }

    @Test
    @DisplayName("GET /history/{email} - Should return list of history records ordered by analyzed_at DESC")
    void testGetHistorySuccess() throws Exception {
        ResumeHistory h1 = new ResumeHistory(TEST_EMAIL, "Google", "Java Developer", 85.5, "2026-10-01 10:00:00", sampleAnalysisResultJson());
        ResumeHistory h2 = new ResumeHistory(TEST_EMAIL, "Amazon", "Backend Engineer", 90.0, "2026-10-02 12:00:00", sampleAnalysisResultJson());
        resumeHistoryRepository.saveAll(List.of(h1, h2));

        mockMvc.perform(get("/history/" + TEST_EMAIL))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].company", is("Amazon")))
                .andExpect(jsonPath("$[0].role", is("Backend Engineer")))
                .andExpect(jsonPath("$[0].ats_score", is(90.0)))
                .andExpect(jsonPath("$[0].analyzed_at", is("2026-10-02 12:00:00")))
                .andExpect(jsonPath("$[0].result_json").doesNotExist())
                .andExpect(jsonPath("$[1].company", is("Google")))
                .andExpect(jsonPath("$[1].role", is("Java Developer")))
                .andExpect(jsonPath("$[1].ats_score", is(85.5)))
                .andExpect(jsonPath("$[1].analyzed_at", is("2026-10-01 10:00:00")));
    }

    @Test
    @DisplayName("GET /history/{email} - Should return empty array when user has no history")
    void testGetHistoryEmpty() throws Exception {
        mockMvc.perform(get("/history/nonexistent_user@example.com"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /history/{email} - Should normalize email case and whitespace")
    void testGetHistoryNormalizedEmail() throws Exception {
        ResumeHistory h1 = new ResumeHistory(TEST_EMAIL, "Netflix", "Platform Engineer", 95.0, "2026-10-03 14:00:00", sampleAnalysisResultJson());
        resumeHistoryRepository.save(h1);

        mockMvc.perform(get("/history/  HISTORY_TEST_USER@EXAMPLE.COM  "))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].company", is("Netflix")));
    }

    @Test
    @DisplayName("GET /history/result/{id} - Should return full parsed 18-field analysis response")
    void testGetHistoryResultSuccess() throws Exception {
        ResumeHistory h = new ResumeHistory(TEST_EMAIL, "Google", "Java Developer", 85.5, "2026-10-01 10:00:00", sampleAnalysisResultJson());
        ResumeHistory saved = resumeHistoryRepository.save(h);

        MvcResult result = mockMvc.perform(get("/history/result/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.filename", is("resume.pdf")))
                .andExpect(jsonPath("$.company", is("Google")))
                .andExpect(jsonPath("$.role", is("Java Developer")))
                .andExpect(jsonPath("$.target_company", is("Google")))
                .andExpect(jsonPath("$.target_role", is("Java Developer")))
                .andExpect(jsonPath("$.ats_score", is(85.5)))
                .andExpect(jsonPath("$.resume_summary.skills", hasItems("Java", "Spring Boot", "SQL")))
                .andExpect(jsonPath("$.job_skills", hasItems("Java", "Spring Boot", "SQL", "Docker")))
                .andExpect(jsonPath("$.matched_skills", hasItems("Java", "Spring Boot", "SQL")))
                .andExpect(jsonPath("$.missing_skills", hasItems("Docker")))
                .andExpect(jsonPath("$.score_breakdown.skills", is(75)))
                .andExpect(jsonPath("$.recommended_roles", hasItems("Backend Developer", "Java Developer")))
                .andExpect(jsonPath("$.top_matches", hasSize(2)))
                .andExpect(jsonPath("$.analysis_time", is(0.45)))
                .andExpect(jsonPath("$.resume_health", hasItem("🟢 Strong technical skills")))
                .andExpect(jsonPath("$.suggestions", hasItem("Add Docker experience")))
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        assertTrue(root.has("filename"));
        assertTrue(root.has("company"));
        assertTrue(root.has("role"));
        assertTrue(root.has("target_company"));
        assertTrue(root.has("target_role"));
        assertTrue(root.has("resume_summary"));
        assertTrue(root.has("job_skills"));
        assertTrue(root.has("matched_skills"));
        assertTrue(root.has("missing_skills"));
        assertTrue(root.has("ats_score"));
        assertTrue(root.has("score_breakdown"));
        assertTrue(root.has("recommended_roles"));
        assertTrue(root.has("top_matches"));
        assertTrue(root.has("analysis_time"));
        assertTrue(root.has("resume_health"));
        assertTrue(root.has("suggestions"));
        assertTrue(root.has("best_resume"));
        assertTrue(root.has("resume_improvement"));
    }

    @Test
    @DisplayName("GET /history/result/{id} - Should return 404 when ID does not exist")
    void testGetHistoryResultNotFound() throws Exception {
        mockMvc.perform(get("/history/result/999999999"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.detail", is("History record not found")));
    }

    @Test
    @DisplayName("DELETE /history/{id} - Should delete existing record and return 200")
    void testDeleteHistorySuccess() throws Exception {
        ResumeHistory h = new ResumeHistory(TEST_EMAIL, "Meta", "Software Engineer", 88.0, "2026-10-01 10:00:00", sampleAnalysisResultJson());
        ResumeHistory saved = resumeHistoryRepository.save(h);
        Long id = saved.getId();

        Notification notif = new Notification(TEST_EMAIL, "Resume analyzed for Meta", "analysis", 0, "2026-10-01 10:00:00");
        notificationRepository.save(notif);

        mockMvc.perform(delete("/history/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("History deleted successfully")));

        assertFalse(resumeHistoryRepository.findById(id).isPresent());
        // Verify notification is untouched
        List<Notification> notifs = notificationRepository.findAll().stream()
                .filter(n -> TEST_EMAIL.equalsIgnoreCase(n.getEmail()))
                .toList();
        assertEquals(1, notifs.size());
    }

    @Test
    @DisplayName("DELETE /history/{id} - Nonexistent ID should still return 200 (idempotent)")
    void testDeleteHistoryNonexistentId() throws Exception {
        mockMvc.perform(delete("/history/999999999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("History deleted successfully")));
    }
}
