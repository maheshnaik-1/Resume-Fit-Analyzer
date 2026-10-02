package com.resumeanalyzer.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.resumeanalyzer.dto.*;
import com.resumeanalyzer.entity.ResumeHistory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AnalysisAssemblyServiceTest {

    private AnalysisAssemblyService assemblyService;
    private ResumeImprovementService improvementService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        ResumeSectionParserService sectionParserService = new ResumeSectionParserService();
        SkillEngineService skillEngineService = new SkillEngineService();
        AtsScoringService atsScoringService = new AtsScoringService();
        RoleMatchingService roleMatchingService = new RoleMatchingService();
        SuggestionService suggestionService = new SuggestionService();
        ResumeHealthService resumeHealthService = new ResumeHealthService();
        improvementService = new ResumeImprovementService();

        assemblyService = new AnalysisAssemblyService(
                sectionParserService,
                skillEngineService,
                atsScoringService,
                roleMatchingService,
                suggestionService,
                resumeHealthService,
                improvementService
        );

        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("1. End-to-end analysis assembly with realistic resume and job description")
    void testAssembleAnalysisRealistic() {
        String resumeText = "Experienced Java Developer with strong Spring Boot, Hibernate, MySQL, SQL, REST API, Git, and OOP skills. "
                + "Education: B.Tech in Computer Science Engineering. "
                + "Certifications: AWS, Java Programming. "
                + "Completed 2 major projects involving microservices architecture.";

        String jobDescription = "We are seeking a Java Developer with experience in Java, Spring Boot, MySQL, REST API, and Docker.";

        BestResumeDto bestResume = new BestResumeDto("Google", "Java Developer", 88.5, "2026-10-01 12:00:00");
        ResumeImprovementDto improvement = new ResumeImprovementDto(
                List.of(new ResumeImprovementHistoryDto(70.0, "2026-09-20"), new ResumeImprovementHistoryDto(80.0, "2026-10-01")),
                70.0, 80.0, 10.0
        );

        AnalysisResponseDto response = assemblyService.assembleAnalysis(
                "my_resume.pdf",
                resumeText,
                jobDescription,
                "google",
                "java developer",
                0.15,
                bestResume,
                improvement
        );

        assertNotNull(response);
        assertEquals("my_resume.pdf", response.getFilename());
        assertEquals("Google", response.getCompany());
        assertEquals("java developer", response.getRole());
        assertEquals("Google", response.getTargetCompany());
        assertEquals("java developer", response.getTargetRole());
        assertEquals(0.15, response.getAnalysisTime());

        // Resume summary
        ResumeSummaryDto summary = response.getResumeSummary();
        assertNotNull(summary);
        assertTrue(summary.getEducation().contains("B.Tech"));
        assertTrue(summary.getCertifications().contains("AWS"));
        assertTrue(summary.getSkills().contains("Java"));

        // Job & Matched skills (4/5 matched: Java, Spring Boot, MySQL, REST API; Docker missing)
        assertEquals(5, response.getJobSkills().size());
        assertEquals(4, response.getMatchedSkills().size());
        assertEquals(List.of("Docker"), response.getMissingSkills());

        // Score Breakdown
        ScoreBreakdownDto breakdown = response.getScoreBreakdown();
        assertEquals(80, breakdown.getSkills()); // 4/5 = 80%
        assertEquals(50, breakdown.getProjects()); // detected projects list has size 1 ("3 Projects Detected") = 50%
        assertEquals(100, breakdown.getEducation()); // B.Tech = 100%
        assertEquals(75, breakdown.getCertifications()); // 2 certs = 75%

        // Weighted ATS: 80*0.70 (56.0) + 50*0.15 (7.5) + 100*0.10 (10.0) + 75*0.05 (3.75) = 77.25
        assertEquals(77.25, response.getAtsScore(), 0.001);

        // Top matches & recommendations
        assertEquals(3, response.getTopMatches().size());
        assertEquals("Java Developer", response.getTopMatches().get(0).getRole());
        assertEquals(3, response.getRecommendedRoles().size());
        assertEquals("Java Developer", response.getRecommendedRoles().get(0));

        // Resume health
        assertEquals(4, response.getResumeHealth().size());
        assertEquals("🟢 Strong technical skills", response.getResumeHealth().get(0));

        // Suggestions
        assertNotNull(response.getSuggestions());
        assertFalse(response.getSuggestions().isEmpty());

        // Best resume & improvement
        assertEquals(bestResume, response.getBestResume());
        assertEquals(improvement, response.getResumeImprovement());
    }

    @Test
    @DisplayName("2. JSON serialization preserves exact 18 snake_case keys matching Python V1.0 contract")
    void testJsonSerializationParity() throws Exception {
        AnalysisResponseDto response = assemblyService.assembleAnalysis(
                "test.pdf",
                "Python FastAPI developer with B.Tech and 1 project",
                "Python FastAPI",
                "amazon",
                "SDE-1",
                0.22
        );

        String json = objectMapper.writeValueAsString(response);
        JsonNode root = objectMapper.readTree(json);

        // Verify all 18 top-level keys
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

        // Verify nested resume_summary keys
        JsonNode resumeSummary = root.get("resume_summary");
        assertTrue(resumeSummary.has("education"));
        assertTrue(resumeSummary.has("skills"));
        assertTrue(resumeSummary.has("projects"));
        assertTrue(resumeSummary.has("certifications"));

        // Verify nested score_breakdown keys
        JsonNode scoreBreakdown = root.get("score_breakdown");
        assertTrue(scoreBreakdown.has("skills"));
        assertTrue(scoreBreakdown.has("projects"));
        assertTrue(scoreBreakdown.has("education"));
        assertTrue(scoreBreakdown.has("certifications"));
    }

    @Test
    @DisplayName("3. BestResume and ResumeImprovement calculation from entity lists")
    void testBestResumeAndImprovementCalculation() {
        ResumeHistory h1 = new ResumeHistory();
        h1.setCompany("Amazon");
        h1.setRole("SDE-1");
        h1.setAtsScore(65.0);
        h1.setAnalyzedAt("2026-09-01 10:00:00");

        ResumeHistory h2 = new ResumeHistory();
        h2.setCompany("Amazon");
        h2.setRole("SDE-1");
        h2.setAtsScore(82.5);
        h2.setAnalyzedAt("2026-09-15 10:00:00");

        ResumeHistory h3 = new ResumeHistory();
        h3.setCompany("Google");
        h3.setRole("Backend Developer");
        h3.setAtsScore(90.0);
        h3.setAnalyzedAt("2026-10-01 10:00:00");

        // Best Resume across user history
        BestResumeDto best = improvementService.getBestResumeRecord(List.of(h1, h2, h3));
        assertNotNull(best);
        assertEquals("Google", best.getCompany());
        assertEquals("Backend Developer", best.getRole());
        assertEquals(90.0, best.getAtsScore());

        // Resume Improvement for Amazon SDE-1 (ordered desc in repository: h2, h1)
        ResumeImprovementDto imp = improvementService.calculateResumeImprovement(List.of(h2, h1));
        assertNotNull(imp);
        assertEquals(2, imp.getHistory().size());
        assertEquals(65.0, imp.getFirstScore());
        assertEquals(82.5, imp.getLatestScore());
        assertEquals(17.5, imp.getImprovement()); // 82.5 - 65.0 = 17.5

        // Null safety
        assertNull(improvementService.getBestResumeRecord(null));
        assertNull(improvementService.getBestResumeRecord(Collections.emptyList()));
        assertNull(improvementService.calculateResumeImprovement(null));
        assertNull(improvementService.calculateResumeImprovement(Collections.emptyList()));
    }

    @Test
    @DisplayName("4. Edge cases: Empty JD, empty resume text, null inputs")
    void testEdgeCases() {
        AnalysisResponseDto emptyResponse = assemblyService.assembleAnalysis(
                null,
                "",
                "",
                "",
                "",
                0.0
        );

        assertNotNull(emptyResponse);
        assertEquals("", emptyResponse.getFilename());
        assertEquals("", emptyResponse.getCompany());
        assertEquals("", emptyResponse.getRole());
        assertEquals(0.0, emptyResponse.getAtsScore());
        assertEquals(0, emptyResponse.getMatchedSkills().size());
        assertEquals(0, emptyResponse.getMissingSkills().size());
        assertEquals(3, emptyResponse.getTopMatches().size());
        assertEquals(4, emptyResponse.getResumeHealth().size());
        assertFalse(emptyResponse.getSuggestions().isEmpty());
    }

    @Test
    @DisplayName("5. Determinism: 100 consecutive runs return identical results")
    void testDeterminism() {
        String resume = "Python, FastAPI, Docker, SQL. B.Tech. 1 project.";
        String jd = "Python, FastAPI, SQL.";

        AnalysisResponseDto baseline = assemblyService.assembleAnalysis("doc.pdf", resume, jd, "Meta", "backend engineer", 0.1);

        for (int i = 0; i < 100; i++) {
            AnalysisResponseDto current = assemblyService.assembleAnalysis("doc.pdf", resume, jd, "Meta", "backend engineer", 0.1);
            assertEquals(baseline, current);
        }
    }
}
