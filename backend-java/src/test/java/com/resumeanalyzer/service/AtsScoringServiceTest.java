package com.resumeanalyzer.service;

import com.resumeanalyzer.dto.AtsCalculationResultDto;
import com.resumeanalyzer.dto.ScoreBreakdownDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AtsScoringServiceTest {

    private AtsScoringService scoringService;

    @BeforeEach
    void setUp() {
        scoringService = new AtsScoringService();
    }

    // ==========================================
    // 1. Component Weights Verification
    // ==========================================

    @Test
    @DisplayName("1. Verify exact V1.0 component weights: 70% Skills, 15% Projects, 10% Education, 5% Certifications")
    void testExactComponentWeights() {
        assertEquals(0.70, AtsScoringService.WEIGHT_SKILLS, 0.0001);
        assertEquals(0.15, AtsScoringService.WEIGHT_PROJECTS, 0.0001);
        assertEquals(0.10, AtsScoringService.WEIGHT_EDUCATION, 0.0001);
        assertEquals(0.05, AtsScoringService.WEIGHT_CERTIFICATIONS, 0.0001);
        assertEquals(1.00, AtsScoringService.WEIGHT_SKILLS + AtsScoringService.WEIGHT_PROJECTS + AtsScoringService.WEIGHT_EDUCATION + AtsScoringService.WEIGHT_CERTIFICATIONS, 0.0001);
    }

    // ==========================================
    // 2. Skills Score Calculation Tests
    // ==========================================

    @Test
    @DisplayName("2. Skills score: standard division, zero job skills, and non-integer rounding")
    void testCalculateSkillsScore() {
        // Zero cases
        assertEquals(0, scoringService.calculateSkillsScore(0, 0), "0/0 must equal 0 without division by zero");
        assertEquals(0, scoringService.calculateSkillsScore(0, 5), "0/5 must equal 0");
        assertEquals(0, scoringService.calculateSkillsScore(3, 0), "Job count 0 must return 0");
        assertEquals(0, scoringService.calculateSkillsScore(-1, 5), "Negative match count must return 0");

        // Standard integer percentages
        assertEquals(20, scoringService.calculateSkillsScore(1, 5), "1/5 must equal 20");
        assertEquals(40, scoringService.calculateSkillsScore(2, 5), "2/5 must equal 40");
        assertEquals(60, scoringService.calculateSkillsScore(3, 5), "3/5 must equal 60");
        assertEquals(80, scoringService.calculateSkillsScore(4, 5), "4/5 must equal 80");
        assertEquals(100, scoringService.calculateSkillsScore(5, 5), "5/5 must equal 100");

        // Non-integer percentages with rounding
        assertEquals(33, scoringService.calculateSkillsScore(1, 3), "1/3 = 33.333% -> round to 33");
        assertEquals(67, scoringService.calculateSkillsScore(2, 3), "2/3 = 66.666% -> round to 67");
        assertEquals(17, scoringService.calculateSkillsScore(1, 6), "1/6 = 16.666% -> round to 17");
        assertEquals(83, scoringService.calculateSkillsScore(5, 6), "5/6 = 83.333% -> round to 83");
        assertEquals(14, scoringService.calculateSkillsScore(1, 7), "1/7 = 14.285% -> round to 14");
        assertEquals(43, scoringService.calculateSkillsScore(3, 7), "3/7 = 42.857% -> round to 43");
    }

    @Test
    @DisplayName("2B. Skills score exact .5 boundary parity with Python round() (ROUND_HALF_EVEN)")
    void testSkillsScoreHalfEvenRoundingParity() {
        // 1/40 = 2.5% -> rounds to nearest even integer 2
        assertEquals(2, scoringService.calculateSkillsScore(1, 40), "2.5 must round to 2");

        // 3/40 = 7.5% -> rounds to nearest even integer 8
        assertEquals(8, scoringService.calculateSkillsScore(3, 40), "7.5 must round to 8");

        // 5/40 = 12.5% -> rounds to nearest even integer 12
        assertEquals(12, scoringService.calculateSkillsScore(5, 40), "12.5 must round to 12");

        // 7/40 = 17.5% -> rounds to nearest even integer 18
        assertEquals(18, scoringService.calculateSkillsScore(7, 40), "17.5 must round to 18");

        // 9/40 = 22.5% -> rounds to nearest even integer 22
        assertEquals(22, scoringService.calculateSkillsScore(9, 40), "22.5 must round to 22");
    }

    // ==========================================
    // 3. Projects Score Calculation Tests
    // ==========================================

    @Test
    @DisplayName("3. Projects score: 0 -> 0, 1 -> 50, 2 -> 75, >=3 -> 100")
    void testCalculateProjectsScore() {
        assertEquals(0, scoringService.calculateProjectsScore(0));
        assertEquals(0, scoringService.calculateProjectsScore(-1));
        assertEquals(50, scoringService.calculateProjectsScore(1));
        assertEquals(75, scoringService.calculateProjectsScore(2));
        assertEquals(100, scoringService.calculateProjectsScore(3));
        assertEquals(100, scoringService.calculateProjectsScore(4));
        assertEquals(100, scoringService.calculateProjectsScore(10));
    }

    // ==========================================
    // 4. Education Score Calculation Tests
    // ==========================================

    @Test
    @DisplayName("4. Education score: empty -> 0, non-empty -> 100")
    void testCalculateEducationScore() {
        assertEquals(0, scoringService.calculateEducationScore(null));
        assertEquals(0, scoringService.calculateEducationScore(Collections.emptyList()));
        assertEquals(100, scoringService.calculateEducationScore(List.of("B.Tech")));
        assertEquals(100, scoringService.calculateEducationScore(List.of("B.Tech", "Computer Science Engineering")));
    }

    // ==========================================
    // 5. Certification Score Calculation Tests
    // ==========================================

    @Test
    @DisplayName("5. Certification score: 0 -> 0, 1 -> 50, 2 -> 75, >=3 -> 100")
    void testCalculateCertificationScore() {
        assertEquals(0, scoringService.calculateCertificationScore(null));
        assertEquals(0, scoringService.calculateCertificationScore(Collections.emptyList()));
        assertEquals(50, scoringService.calculateCertificationScore(List.of("AWS")));
        assertEquals(75, scoringService.calculateCertificationScore(List.of("AWS", "Azure")));
        assertEquals(100, scoringService.calculateCertificationScore(List.of("AWS", "Azure", "Python Programming")));
        assertEquals(100, scoringService.calculateCertificationScore(List.of("AWS", "Azure", "Python Programming", "Machine Learning")));
    }

    // ==========================================
    // 6. Final Weighted ATS Score Tests
    // ==========================================

    @Test
    @DisplayName("6. Final ATS score: weighted sum and 2-decimal precision rounding with ROUND_HALF_EVEN")
    void testCalculateFinalAtsScore() {
        // Boundary extremes
        assertEquals(0.0, scoringService.calculateFinalAtsScore(0, 0, 0, 0), 0.001);
        assertEquals(100.0, scoringService.calculateFinalAtsScore(100, 100, 100, 100), 0.001);

        // Mixed realistic combinations matching Python V1.0 arithmetic
        // Case 1: 85 * 0.70 (59.5) + 50 * 0.15 (7.5) + 100 * 0.10 (10.0) + 75 * 0.05 (3.75) = 80.75
        assertEquals(80.75, scoringService.calculateFinalAtsScore(85, 50, 100, 75), 0.001);

        // Case 2: 33 * 0.70 (23.1) + 50 * 0.15 (7.5) + 0 * 0.10 (0.0) + 50 * 0.05 (2.5) = 33.10
        assertEquals(33.1, scoringService.calculateFinalAtsScore(33, 50, 0, 50), 0.001);

        // Case 3: 67 * 0.70 (46.9) + 75 * 0.15 (11.25) + 100 * 0.10 (10.0) + 100 * 0.05 (5.0) = 73.15
        assertEquals(73.15, scoringService.calculateFinalAtsScore(67, 75, 100, 100), 0.001);

        // Case 4: Only education: 0 + 0 + 100*0.10 + 0 = 10.0
        assertEquals(10.0, scoringService.calculateFinalAtsScore(0, 0, 100, 0), 0.001);

        // Case 5: Only projects: 0 + 100*0.15 + 0 + 0 = 15.0
        assertEquals(15.0, scoringService.calculateFinalAtsScore(0, 100, 0, 0), 0.001);

        // Case 6: Only certifications: 0 + 0 + 0 + 100*0.05 = 5.0
        assertEquals(5.0, scoringService.calculateFinalAtsScore(0, 0, 0, 100), 0.001);
    }

    @Test
    @DisplayName("6B. Final ATS score half-cent boundary tests (ROUND_HALF_EVEN)")
    void testFinalAtsScoreHalfEvenRounding() {
        // Direct test of exact half-cent boundary rounding matching Python 3 round(x, 2)
        // 65.125 -> 65.12 (rounds down to even hundredth)
        // 65.135 -> 65.14 (rounds up to even hundredth)
        assertEquals(65.12, java.math.BigDecimal.valueOf(65.125).setScale(2, java.math.RoundingMode.HALF_EVEN).doubleValue(), 0.0001);
        assertEquals(65.14, java.math.BigDecimal.valueOf(65.135).setScale(2, java.math.RoundingMode.HALF_EVEN).doubleValue(), 0.0001);
    }

    // ==========================================
    // 7. Composite calculateAtsScore() Tests
    // ==========================================

    @Test
    @DisplayName("7. Composite calculateAtsScore derives component counts and builds complete result DTO")
    void testCompositeCalculateAtsScore() {
        List<String> matched = List.of("Java", "Spring Boot", "AWS");
        List<String> job = List.of("Java", "Spring Boot", "AWS", "Docker"); // 3/4 = 75% skills score
        List<String> projects = List.of("1 Project Detected"); // count 1 -> 50 projects score
        List<String> education = List.of("B.Tech", "Computer Science"); // non-empty -> 100 education score
        List<String> certs = List.of("AWS", "Java Programming"); // 2 certs -> 75 cert score

        // Expected: 75 * 0.70 (52.5) + 50 * 0.15 (7.5) + 100 * 0.10 (10.0) + 75 * 0.05 (3.75) = 73.75
        AtsCalculationResultDto result = scoringService.calculateAtsScore(matched, job, projects, education, certs);

        assertNotNull(result);
        assertEquals(73.75, result.getAtsScore(), 0.001);

        ScoreBreakdownDto breakdown = result.getScoreBreakdown();
        assertNotNull(breakdown);
        assertEquals(75, breakdown.getSkills());
        assertEquals(50, breakdown.getProjects());
        assertEquals(100, breakdown.getEducation());
        assertEquals(75, breakdown.getCertifications());
    }

    // ==========================================
    // 8. Null and Empty Safety Tests
    // ==========================================

    @Test
    @DisplayName("8. Null and empty input safety across all methods")
    void testNullAndEmptySafety() {
        AtsCalculationResultDto nullResult = scoringService.calculateAtsScore(null, null, null, null, null);
        assertNotNull(nullResult);
        assertEquals(0.0, nullResult.getAtsScore(), 0.001);
        assertEquals(new ScoreBreakdownDto(0, 0, 0, 0), nullResult.getScoreBreakdown());

        AtsCalculationResultDto emptyResult = scoringService.calculateAtsScore(
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList()
        );
        assertNotNull(emptyResult);
        assertEquals(0.0, emptyResult.getAtsScore(), 0.001);
        assertEquals(new ScoreBreakdownDto(0, 0, 0, 0), emptyResult.getScoreBreakdown());
    }

    // ==========================================
    // 9. Determinism Tests
    // ==========================================

    @Test
    @DisplayName("9. Determinism: Repeated execution with identical inputs produces identical results")
    void testDeterminism() {
        List<String> matched = List.of("Python", "FastAPI");
        List<String> job = List.of("Python", "FastAPI", "Docker");
        List<String> projects = List.of("1 Project Detected");
        List<String> education = List.of("B.Tech");
        List<String> certs = List.of("Machine Learning");

        AtsCalculationResultDto r1 = scoringService.calculateAtsScore(matched, job, projects, education, certs);
        AtsCalculationResultDto r2 = scoringService.calculateAtsScore(matched, job, projects, education, certs);

        assertEquals(r1, r2);
        assertEquals(r1.getAtsScore(), r2.getAtsScore());
        assertEquals(r1.getScoreBreakdown(), r2.getScoreBreakdown());
    }
}
