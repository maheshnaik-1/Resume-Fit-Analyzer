package com.resumeanalyzer.service;

import com.resumeanalyzer.catalog.RoleCatalog;
import com.resumeanalyzer.dto.RoleMatchDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class RoleMatchingServiceTest {

    private RoleMatchingService roleMatchingService;

    @BeforeEach
    void setUp() {
        roleMatchingService = new RoleMatchingService();
    }

    // ==========================================
    // 1. Role Catalog Verification
    // ==========================================

    @Test
    @DisplayName("1. Verify exact 18 roles, exact role names, skill lists, and catalog ordering")
    void testRoleCatalogIntegrity() {
        assertEquals(18, RoleCatalog.ROLE_SKILLS.size(), "Catalog must contain exactly 18 roles");

        List<String> expectedRoles = List.of(
                "Software Engineer",
                "Java Developer",
                "Python Developer",
                "Backend Developer",
                "Frontend Developer",
                "Full Stack Developer",
                "React Developer",
                "Data Analyst",
                "Data Engineer",
                "Cloud Engineer",
                "DevOps Engineer",
                "Machine Learning Engineer",
                "AI Engineer",
                "Spring Boot Developer",
                "iOS Developer",
                "Embedded Systems Engineer",
                "Cyber Security Analyst",
                "Testing Engineer"
        );

        List<String> actualRoles = new ArrayList<>(RoleCatalog.ROLE_SKILLS.keySet());
        assertEquals(expectedRoles, actualRoles, "Role names and insertion order must match Python V1.0 exactly");

        // Verify specific skill list counts and content
        assertEquals(9, RoleCatalog.ROLE_SKILLS.get("Software Engineer").size());
        assertEquals(List.of("Java", "Python", "C++", "OOP", "Algorithms", "Data Structures", "DSA", "Git", "SQL"),
                RoleCatalog.ROLE_SKILLS.get("Software Engineer"));

        assertEquals(8, RoleCatalog.ROLE_SKILLS.get("Java Developer").size());
        assertEquals(List.of("Java", "Spring Boot", "Hibernate", "REST API", "MySQL", "SQL", "Git", "OOP"),
                RoleCatalog.ROLE_SKILLS.get("Java Developer"));

        assertEquals(6, RoleCatalog.ROLE_SKILLS.get("Python Developer").size());
        assertEquals(List.of("Python", "FastAPI", "REST API", "SQL", "Git", "OOP"),
                RoleCatalog.ROLE_SKILLS.get("Python Developer"));

        assertEquals(8, RoleCatalog.ROLE_SKILLS.get("Backend Developer").size());
        assertEquals(7, RoleCatalog.ROLE_SKILLS.get("Frontend Developer").size());
        assertEquals(8, RoleCatalog.ROLE_SKILLS.get("Full Stack Developer").size());
        assertEquals(6, RoleCatalog.ROLE_SKILLS.get("React Developer").size());
        assertEquals(7, RoleCatalog.ROLE_SKILLS.get("Data Analyst").size());
        assertEquals(7, RoleCatalog.ROLE_SKILLS.get("Data Engineer").size());
        assertEquals(6, RoleCatalog.ROLE_SKILLS.get("Cloud Engineer").size());
        assertEquals(6, RoleCatalog.ROLE_SKILLS.get("DevOps Engineer").size());
        assertEquals(6, RoleCatalog.ROLE_SKILLS.get("Machine Learning Engineer").size());
        assertEquals(6, RoleCatalog.ROLE_SKILLS.get("AI Engineer").size());
        assertEquals(6, RoleCatalog.ROLE_SKILLS.get("Spring Boot Developer").size());
        assertEquals(6, RoleCatalog.ROLE_SKILLS.get("iOS Developer").size());
        assertEquals(6, RoleCatalog.ROLE_SKILLS.get("Embedded Systems Engineer").size());
        assertEquals(6, RoleCatalog.ROLE_SKILLS.get("Cyber Security Analyst").size());
        assertEquals(6, RoleCatalog.ROLE_SKILLS.get("Testing Engineer").size());
    }

    // ==========================================
    // 2. 100% Role Match Verification
    // ==========================================

    @Test
    @DisplayName("2. 100% match when all required skills for a role are present")
    void testFullRoleMatch() {
        List<String> javaDevSkills = List.of(
                "Java", "Spring Boot", "Hibernate", "REST API", "MySQL", "SQL", "Git", "OOP"
        );

        List<RoleMatchDto> matches = roleMatchingService.matchRoles(javaDevSkills, 3);
        assertNotNull(matches);
        assertFalse(matches.isEmpty());

        RoleMatchDto topMatch = matches.get(0);
        assertEquals("Java Developer", topMatch.getRole());
        assertEquals(100.0, topMatch.getMatchScore(), 0.0001);
    }

    // ==========================================
    // 3. Partial Matching & Rounding Parity
    // ==========================================

    @Test
    @DisplayName("3. Exact Python HALF_EVEN rounding parity for known fraction ratios")
    void testPartialMatchingAndRoundingParity() {
        // 1/7 = 14.2857... -> 14.29 (Frontend Developer with 1 skill: "HTML")
        List<RoleMatchDto> m1 = roleMatchingService.matchRoles(List.of("HTML"), 18);
        RoleMatchDto frontendMatch = m1.stream().filter(r -> "Frontend Developer".equals(r.getRole())).findFirst().orElseThrow();
        assertEquals(14.29, frontendMatch.getMatchScore(), 0.0001);

        // 2/7 = 28.5714... -> 28.57 (Frontend Developer with 2 skills: "HTML", "CSS")
        List<RoleMatchDto> m2 = roleMatchingService.matchRoles(List.of("HTML", "CSS"), 18);
        RoleMatchDto frontendMatch2 = m2.stream().filter(r -> "Frontend Developer".equals(r.getRole())).findFirst().orElseThrow();
        assertEquals(28.57, frontendMatch2.getMatchScore(), 0.0001);

        // 3/8 = 37.50 (Java Developer with 3 skills: "Java", "Spring Boot", "Git")
        List<RoleMatchDto> m3 = roleMatchingService.matchRoles(List.of("Java", "Spring Boot", "Git"), 18);
        RoleMatchDto javaDevMatch = m3.stream().filter(r -> "Java Developer".equals(r.getRole())).findFirst().orElseThrow();
        assertEquals(37.5, javaDevMatch.getMatchScore(), 0.0001);

        // 1/6 = 16.6666... -> 16.67 (Python Developer with 1 skill: "FastAPI")
        List<RoleMatchDto> m4 = roleMatchingService.matchRoles(List.of("FastAPI"), 18);
        RoleMatchDto pythonDevMatch = m4.stream().filter(r -> "Python Developer".equals(r.getRole())).findFirst().orElseThrow();
        assertEquals(16.67, pythonDevMatch.getMatchScore(), 0.0001);

        // 5/6 = 83.3333... -> 83.33 (Python Developer with 5 skills: "Python", "FastAPI", "REST API", "SQL", "Git")
        List<RoleMatchDto> m5 = roleMatchingService.matchRoles(List.of("Python", "FastAPI", "REST API", "SQL", "Git"), 18);
        RoleMatchDto pythonDevMatch5 = m5.stream().filter(r -> "Python Developer".equals(r.getRole())).findFirst().orElseThrow();
        assertEquals(83.33, pythonDevMatch5.getMatchScore(), 0.0001);

        // 1/9 = 11.1111... -> 11.11 (Software Engineer with 1 skill: "Algorithms")
        List<RoleMatchDto> m6 = roleMatchingService.matchRoles(List.of("Algorithms"), 18);
        RoleMatchDto sweMatch = m6.stream().filter(r -> "Software Engineer".equals(r.getRole())).findFirst().orElseThrow();
        assertEquals(11.11, sweMatch.getMatchScore(), 0.0001);
    }

    // ==========================================
    // 4. Duplicate Skills Deduplication
    // ==========================================

    @Test
    @DisplayName("4. Duplicate detected skills must not inflate match scores")
    void testDuplicateSkillsDeduplication() {
        List<String> singleSkills = List.of("Java", "Spring Boot");
        List<String> duplicateSkills = List.of("Java", "Java", "Spring Boot", "Spring Boot", "Java");

        List<RoleMatchDto> singleResults = roleMatchingService.matchRoles(singleSkills, 18);
        List<RoleMatchDto> duplicateResults = roleMatchingService.matchRoles(duplicateSkills, 18);

        assertEquals(singleResults, duplicateResults, "Duplicate input skills must produce identical results to unique set");
    }

    // ==========================================
    // 5. Extra / Unrelated Skills Ignored
    // ==========================================

    @Test
    @DisplayName("5. Extra or unrelated skills not in role requirement profiles are safely ignored")
    void testExtraSkillsIgnored() {
        List<String> relevantSkills = List.of("Swift", "Xcode"); // 2/6 = 33.33% for iOS Developer
        List<String> skillsWithExtra = List.of("Swift", "Xcode", "Communication", "Problem Solving", "UnknownSkill");

        List<RoleMatchDto> res1 = roleMatchingService.matchRoles(relevantSkills, 18);
        List<RoleMatchDto> res2 = roleMatchingService.matchRoles(skillsWithExtra, 18);

        RoleMatchDto ios1 = res1.stream().filter(r -> "iOS Developer".equals(r.getRole())).findFirst().orElseThrow();
        RoleMatchDto ios2 = res2.stream().filter(r -> "iOS Developer".equals(r.getRole())).findFirst().orElseThrow();

        assertEquals(33.33, ios1.getMatchScore(), 0.0001);
        assertEquals(33.33, ios2.getMatchScore(), 0.0001);
        assertEquals(ios1, ios2);
    }

    // ==========================================
    // 6. Zero / Empty / Null Input Behavior
    // ==========================================

    @Test
    @DisplayName("6. Empty input list yields 18 roles with 0.0%, top 3 is alphabetical (AI Engineer, Backend Developer, Cloud Engineer)")
    void testEmptyInputBehavior() {
        List<RoleMatchDto> matches = roleMatchingService.matchRoles(Collections.emptyList(), 3);
        assertEquals(3, matches.size());

        assertEquals("AI Engineer", matches.get(0).getRole());
        assertEquals(0.0, matches.get(0).getMatchScore(), 0.0001);

        assertEquals("Backend Developer", matches.get(1).getRole());
        assertEquals(0.0, matches.get(1).getMatchScore(), 0.0001);

        assertEquals("Cloud Engineer", matches.get(2).getRole());
        assertEquals(0.0, matches.get(2).getMatchScore(), 0.0001);
    }

    @Test
    @DisplayName("7. Null input handled safely and normalized to empty-list behavior")
    void testNullInputBehavior() {
        List<RoleMatchDto> matches = roleMatchingService.matchRoles(null, 3);
        assertEquals(3, matches.size());

        assertEquals("AI Engineer", matches.get(0).getRole());
        assertEquals(0.0, matches.get(0).getMatchScore(), 0.0001);

        assertEquals("Backend Developer", matches.get(1).getRole());
        assertEquals(0.0, matches.get(1).getMatchScore(), 0.0001);

        assertEquals("Cloud Engineer", matches.get(2).getRole());
        assertEquals(0.0, matches.get(2).getMatchScore(), 0.0001);
    }

    // ==========================================
    // 7. Tie-Breaking & Sorting Verification
    // ==========================================

    @Test
    @DisplayName("8. Tie-breaking sorts descending by score, then ascending ASCII by role name")
    void testTieBreakingAndAsciiOrdering() {
        // When all 18 roles have 0% match, verify complete alphabetical tie order
        List<RoleMatchDto> allTied = roleMatchingService.matchRoles(Collections.emptyList(), 18);
        assertEquals(18, allTied.size());

        for (RoleMatchDto r : allTied) {
            assertEquals(0.0, r.getMatchScore(), 0.0001);
        }

        List<String> roleNames = roleMatchingService.extractRecommendedRoles(allTied);

        // Verify that "Testing Engineer" (ASCII 'T'=84) precedes "iOS Developer" (ASCII 'i'=105)
        int testingIdx = roleNames.indexOf("Testing Engineer");
        int iosIdx = roleNames.indexOf("iOS Developer");
        assertTrue(testingIdx < iosIdx, "Testing Engineer must precede iOS Developer in natural ASCII ordering");

        // Verify full alphabetical order
        List<String> sortedCopy = new ArrayList<>(roleNames);
        Collections.sort(sortedCopy);
        assertEquals(sortedCopy, roleNames, "Tied roles must be sorted alphabetically by role name");
    }

    // ==========================================
    // 8. Top-N Slicing Verification
    // ==========================================

    @Test
    @DisplayName("9. Python slice parity for top_n: 0, 1, 3, 5, 18, 50, -1, -5, -18, -25")
    void testTopNSlicing() {
        List<String> skills = List.of("Python", "FastAPI", "SQL", "Git", "Docker");
        List<RoleMatchDto> all18 = roleMatchingService.matchRoles(skills, 18);
        assertEquals(18, all18.size());

        // Standard positive slicing
        assertEquals(0, roleMatchingService.matchRoles(skills, 0).size());
        assertEquals(1, roleMatchingService.matchRoles(skills, 1).size());
        assertEquals(3, roleMatchingService.matchRoles(skills, 3).size());
        assertEquals(3, roleMatchingService.matchRoles(skills).size()); // default = 3
        assertEquals(5, roleMatchingService.matchRoles(skills, 5).size());
        assertEquals(18, roleMatchingService.matchRoles(skills, 18).size());
        assertEquals(18, roleMatchingService.matchRoles(skills, 50).size(), "top_n > 18 yields all 18 roles");

        // Negative slicing matching Python list[:top_n]
        // top_n = -1 -> 18 - 1 = 17 elements
        List<RoleMatchDto> sliceMinus1 = roleMatchingService.matchRoles(skills, -1);
        assertEquals(17, sliceMinus1.size());
        assertEquals(all18.subList(0, 17), sliceMinus1);

        // top_n = -5 -> 18 - 5 = 13 elements
        List<RoleMatchDto> sliceMinus5 = roleMatchingService.matchRoles(skills, -5);
        assertEquals(13, sliceMinus5.size());
        assertEquals(all18.subList(0, 13), sliceMinus5);

        // top_n = -18 -> 18 - 18 = 0 elements
        assertEquals(0, roleMatchingService.matchRoles(skills, -18).size());

        // top_n = -25 -> <= 0 elements
        assertEquals(0, roleMatchingService.matchRoles(skills, -25).size());
    }

    // ==========================================
    // 9. extractRecommendedRoles Helper
    // ==========================================

    @Test
    @DisplayName("10. extractRecommendedRoles preserves exact top-matches ranking order")
    void testExtractRecommendedRoles() {
        List<RoleMatchDto> matches = List.of(
                new RoleMatchDto("Java Developer", 75.0),
                new RoleMatchDto("Backend Developer", 62.5),
                new RoleMatchDto("Spring Boot Developer", 50.0)
        );

        List<String> expected = List.of("Java Developer", "Backend Developer", "Spring Boot Developer");
        List<String> actual = roleMatchingService.extractRecommendedRoles(matches);

        assertEquals(expected, actual);
        assertEquals(Collections.emptyList(), roleMatchingService.extractRecommendedRoles(null));
        assertEquals(Collections.emptyList(), roleMatchingService.extractRecommendedRoles(Collections.emptyList()));
    }

    // ==========================================
    // 10. Determinism Verification
    // ==========================================

    @Test
    @DisplayName("11. Determinism: 100 consecutive invocations produce strictly identical results")
    void testDeterminism() {
        List<String> skills = List.of("Java", "Spring Boot", "MySQL", "AWS", "Docker");

        List<RoleMatchDto> baseline = roleMatchingService.matchRoles(skills, 3);
        for (int i = 0; i < 100; i++) {
            List<RoleMatchDto> current = roleMatchingService.matchRoles(skills, 3);
            assertEquals(baseline, current, "Result at iteration " + i + " must match baseline identically");
        }
    }
}
