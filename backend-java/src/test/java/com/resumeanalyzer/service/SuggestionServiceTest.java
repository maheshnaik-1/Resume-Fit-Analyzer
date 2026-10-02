package com.resumeanalyzer.service;

import com.resumeanalyzer.catalog.CompanySuggestionCatalog;
import com.resumeanalyzer.dto.RoleMatchDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SuggestionServiceTest {

    private SuggestionService suggestionService;

    @BeforeEach
    void setUp() {
        suggestionService = new SuggestionService();
    }

    @Test
    @DisplayName("1. Career match suggestions: top score >= 60 vs < 60 vs 0")
    void testCareerMatchSuggestions() {
        // High match (>= 60)
        List<RoleMatchDto> highMatches = List.of(new RoleMatchDto("Java Developer", 75.0));
        List<String> s1 = suggestionService.generateSuggestions(highMatches, Collections.emptyList(), 85.0, List.of("p1", "p2"), List.of("c1"), "unknown", "unknown");
        assertTrue(s1.contains("Your resume has the highest skill match for the 'Java Developer' role (75.0% match)."));
        assertFalse(s1.contains("Consider adding more core technical skills to improve your role match score."));

        // Low match (< 60 but > 0)
        List<RoleMatchDto> lowMatches = List.of(new RoleMatchDto("Python Developer", 33.33));
        List<String> s2 = suggestionService.generateSuggestions(lowMatches, Collections.emptyList(), 85.0, List.of("p1", "p2"), List.of("c1"), "unknown", "unknown");
        assertTrue(s2.contains("Your resume has the highest skill match for the 'Python Developer' role (33.33% match)."));
        assertTrue(s2.contains("Consider adding more core technical skills to improve your role match score."));

        // Zero match (0.0)
        List<RoleMatchDto> zeroMatches = List.of(new RoleMatchDto("AI Engineer", 0.0));
        List<String> s3 = suggestionService.generateSuggestions(zeroMatches, Collections.emptyList(), 85.0, List.of("p1", "p2"), List.of("c1"), "unknown", "unknown");
        assertTrue(s3.contains("Add more relevant technical skills to your resume to improve role match scores."));

        // Empty matches
        List<String> s4 = suggestionService.generateSuggestions(Collections.emptyList(), Collections.emptyList(), 85.0, List.of("p1", "p2"), List.of("c1"), "unknown", "unknown");
        assertTrue(s4.contains("Add more relevant technical skills to your resume to improve role match scores."));
    }

    @Test
    @DisplayName("2. Missing skills suggestions loop")
    void testMissingSkillsSuggestions() {
        List<String> missing = List.of("Docker", "Kubernetes", "AWS");
        List<String> s = suggestionService.generateSuggestions(Collections.emptyList(), missing, 85.0, List.of("p1", "p2"), List.of("c1"), "unknown", "unknown");

        assertTrue(s.contains("Learn Docker through practical projects."));
        assertTrue(s.contains("Learn Kubernetes through practical projects."));
        assertTrue(s.contains("Learn AWS through practical projects."));
    }

    @Test
    @DisplayName("3. ATS score bracket suggestions: < 60 vs < 80 vs >= 80")
    void testAtsScoreBracketSuggestions() {
        // ATS < 60
        List<String> sLow = suggestionService.generateSuggestions(Collections.emptyList(), Collections.emptyList(), 45.0, List.of("p1", "p2"), List.of("c1"), "unknown", "unknown");
        assertTrue(sLow.contains("Build 1-2 projects related to your target job role."));
        assertTrue(sLow.contains("Tailor your resume for each job application."));
        assertFalse(sLow.contains("Strengthen your technical skills with certifications."));

        // ATS >= 60 and < 80
        List<String> sMed = suggestionService.generateSuggestions(Collections.emptyList(), Collections.emptyList(), 72.5, List.of("p1", "p2"), List.of("c1"), "unknown", "unknown");
        assertTrue(sMed.contains("Strengthen your technical skills with certifications."));
        assertTrue(sMed.contains("Add measurable achievements to your projects."));
        assertFalse(sMed.contains("Build 1-2 projects related to your target job role."));

        // ATS >= 80
        List<String> sHigh = suggestionService.generateSuggestions(Collections.emptyList(), Collections.emptyList(), 88.0, List.of("p1", "p2"), List.of("c1"), "unknown", "unknown");
        assertFalse(sHigh.contains("Build 1-2 projects related to your target job role."));
        assertFalse(sHigh.contains("Strengthen your technical skills with certifications."));
    }

    @Test
    @DisplayName("4. Resume quality checks: projects < 2, certs == 0, and universal recommendations")
    void testResumeQualitySuggestions() {
        List<String> s = suggestionService.generateSuggestions(Collections.emptyList(), Collections.emptyList(), 85.0, List.of("1 Project"), Collections.emptyList(), "unknown", "unknown");

        assertTrue(s.contains("Include more personal or academic projects."));
        assertTrue(s.contains("Earn at least one relevant certification."));
        assertTrue(s.contains("Improve resume keywords for better ATS compatibility."));
        assertTrue(s.contains("Keep your GitHub and LinkedIn profiles updated."));
    }

    @Test
    @DisplayName("5. Company-specific suggestions: specific role, default fallback, and unknown company")
    void testCompanySuggestions() {
        // Google Data Analyst
        List<String> sGoogle = suggestionService.generateSuggestions(Collections.emptyList(), Collections.emptyList(), 85.0, List.of("p1", "p2"), List.of("c1"), "Google", "Data Analyst");
        assertTrue(sGoogle.contains("Improve SQL and Pandas skills with real-world datasets."));
        assertTrue(sGoogle.contains("Build interactive dashboards using Power BI or Looker."));
        assertTrue(sGoogle.contains("Practice statistical analysis and clear data storytelling."));

        // Amazon Default fallback (when role is unknown for company)
        List<String> sAmazon = suggestionService.generateSuggestions(Collections.emptyList(), Collections.emptyList(), 85.0, List.of("p1", "p2"), List.of("c1"), "Amazon", "unknown_role");
        assertTrue(sAmazon.contains("Align your project descriptions and achievements with Amazon Leadership Principles."));

        // Unknown company (skipped without error)
        List<String> sUnknown = suggestionService.generateSuggestions(Collections.emptyList(), Collections.emptyList(), 85.0, List.of("p1", "p2"), List.of("c1"), "SomeStartup", "Engineer");
        assertFalse(sUnknown.isEmpty());
    }
}
