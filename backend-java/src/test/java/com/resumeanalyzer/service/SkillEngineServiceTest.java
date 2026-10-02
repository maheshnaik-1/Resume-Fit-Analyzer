package com.resumeanalyzer.service;

import com.resumeanalyzer.catalog.SkillCatalog;
import com.resumeanalyzer.dto.SkillAnalysisResultDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SkillEngineServiceTest {

    private SkillEngineService skillEngineService;

    @BeforeEach
    void setUp() {
        skillEngineService = new SkillEngineService();
    }

    @Test
    @DisplayName("1. Catalog integrity: Exactly 69 canonical skills and 65 alias mappings")
    void testCatalogIntegrity() {
        assertEquals(69, SkillCatalog.CANONICAL_SKILLS.size(), "Catalog must contain exactly 69 canonical skills");
        assertEquals(65, SkillCatalog.SKILL_ALIASES.size(), "Catalog must contain exactly 65 alias mappings");
        
        // Verify fallback for unmapped skills
        assertEquals(List.of("Microservices"), SkillCatalog.getAliasesForSkill("Microservices"));
        assertEquals(List.of("Linux"), SkillCatalog.getAliasesForSkill("Linux"));
        assertEquals(List.of("Networking"), SkillCatalog.getAliasesForSkill("Networking"));
        assertEquals(List.of("Excel"), SkillCatalog.getAliasesForSkill("Excel"));
    }

    @Test
    @DisplayName("2. All 69 canonical skills are recognized when present in resume text")
    void testAllCanonicalSkillsRecognition() {
        String allSkillsText = String.join(", ", SkillCatalog.CANONICAL_SKILLS);
        List<String> detected = skillEngineService.detectResumeSkills(allSkillsText);

        assertEquals(69, detected.size(), "Every canonical skill should be detected");
        for (String skill : SkillCatalog.CANONICAL_SKILLS) {
            assertTrue(detected.contains(skill), "Expected skill: " + skill + " was not detected");
        }
    }

    @Test
    @DisplayName("3. Alias canonicalization: Aliases map to exact canonical names")
    void testAliasCanonicalization() {
        String resumeText = "Experienced with K8s, EC2, Spring, JS, React.js, Core Java, Python Programming, CPP, and Mongo DB.";
        List<String> detected = skillEngineService.detectResumeSkills(resumeText);

        assertTrue(detected.contains("Kubernetes"), "K8s should map to Kubernetes");
        assertTrue(detected.contains("AWS"), "EC2 should map to AWS");
        assertTrue(detected.contains("Spring Boot"), "Spring should map to Spring Boot");
        assertTrue(detected.contains("JavaScript"), "JS should map to JavaScript");
        assertTrue(detected.contains("React"), "React.js should map to React");
        assertTrue(detected.contains("Java"), "Core Java should map to Java");
        assertTrue(detected.contains("Python"), "Python Programming should map to Python");
        assertTrue(detected.contains("C++"), "CPP should map to C++");
        assertTrue(detected.contains("MongoDB"), "Mongo DB should map to MongoDB");

        // Ensure aliases themselves do not appear as detected skill names
        assertFalse(detected.contains("K8s"));
        assertFalse(detected.contains("EC2"));
        assertFalse(detected.contains("JS"));
        assertFalse(detected.contains("CPP"));
    }

    @Test
    @DisplayName("4. C and C++ regression tests matching Python V1.0 exact behavior")
    void testCAndCppRegressions() {
        // "C" -> C detected
        assertEquals(List.of("C"), skillEngineService.detectResumeSkills("Expert in C"));

        // "C programming" -> C detected
        assertEquals(List.of("C"), skillEngineService.detectResumeSkills("Experienced in C programming"));

        // "C Language" -> C detected
        assertEquals(List.of("C"), skillEngineService.detectResumeSkills("Course in C Language"));

        // "C++" -> C++ detected, C NOT detected
        assertEquals(List.of("C++"), skillEngineService.detectResumeSkills("Expert in C++"));

        // "C#" -> neither C nor C++ detected
        assertTrue(skillEngineService.detectResumeSkills("Developer using C# and .NET").isEmpty());

        // "C-level" -> C IS detected (exact Python V1.0 lookahead behavior)
        assertEquals(List.of("C"), skillEngineService.detectResumeSkills("Working with C-level executives"));

        // "COMPANY" -> neither detected
        assertTrue(skillEngineService.detectResumeSkills("Welcome to our COMPANY").isEmpty());

        // "CSE" -> neither detected
        assertTrue(skillEngineService.detectResumeSkills("Graduated from CSE department").isEmpty());
    }

    @Test
    @DisplayName("5. Important compatibility test: 'C C++ C# C-level CSE COMPANY'")
    void testExactCompatibilityString() {
        String testInput = "C C++ C# C-level CSE COMPANY";
        List<String> detected = skillEngineService.detectResumeSkills(testInput);

        assertEquals(List.of("C", "C++"), detected, "Must detect exactly ['C', 'C++'] preserving V1.0 parity");
    }

    @Test
    @DisplayName("6. Short skill boundary protection: No false positives inside larger words")
    void testShortSkillBoundaryProtection() {
        String text = "COMMUNICATION skills, REACTION times, ADJUST parameters, POWDER coating, SIEMENS automation.";
        List<String> detected = skillEngineService.detectResumeSkills(text);

        // "Communication" is in catalog and should match "COMMUNICATION"
        assertTrue(detected.contains("Communication"));

        // Words containing short substrings must NOT trigger false positives
        assertFalse(detected.contains("React"), "REACTION must not trigger React");
        assertFalse(detected.contains("JavaScript"), "ADJUST must not trigger JS");
        assertFalse(detected.contains("Deep Learning"), "POWDER must not trigger DL");
        assertFalse(detected.contains("SIEM"), "SIEMENS must not trigger SIEM");
        assertFalse(detected.contains("C"), "COMMUNICATION must not trigger C");
    }

    @Test
    @DisplayName("7. Case-insensitive matching")
    void testCaseInsensitivity() {
        String text = "skilled in java, PYTHON, reACt, DoCKeR, and kuBERnetes";
        List<String> detected = skillEngineService.detectResumeSkills(text);

        assertEquals(List.of("Docker", "Java", "Kubernetes", "Python", "React"), detected);
    }

    @Test
    @DisplayName("8. Duplicate prevention: Multiple aliases for the same skill yield only one entry")
    void testDuplicatePrevention() {
        String text = "Proficient in Java, Core Java, and Java SE.";
        List<String> detected = skillEngineService.detectResumeSkills(text);

        assertEquals(1, detected.size());
        assertEquals("Java", detected.get(0));
    }

    @Test
    @DisplayName("9. Resume skills alphabetical ordering")
    void testResumeSkillsAlphabeticalSorting() {
        String text = "Spring Boot, Docker, AWS, Java, C++, Python";
        List<String> detected = skillEngineService.detectResumeSkills(text);

        assertEquals(List.of("AWS", "C++", "Docker", "Java", "Python", "Spring Boot"), detected);
    }

    @Test
    @DisplayName("10. Job description skills detection preserves catalog order (canonical names only)")
    void testJobDescriptionSkillsCatalogOrder() {
        String jobDescription = "Looking for a developer with Python, Docker, Java, and AWS experience.";
        List<String> jobSkills = skillEngineService.detectJobSkills(jobDescription);

        // In catalog: Java (1), Python (2), Docker (19), AWS (22)
        assertEquals(List.of("Java", "Python", "Docker", "AWS"), jobSkills);
    }

    @Test
    @DisplayName("11. Matched and missing skills calculation preserving job skills order")
    void testMatchedAndMissingSkillsOrder() {
        List<String> jobSkills = List.of("Java", "Python", "Docker", "AWS", "Kubernetes");
        List<String> resumeSkills = List.of("AWS", "Java", "Kubernetes"); // Alphabetical

        List<String> matched = skillEngineService.calculateMatchedSkills(jobSkills, resumeSkills);
        List<String> missing = skillEngineService.calculateMissingSkills(jobSkills, resumeSkills);

        // Both preserve jobSkills ordering
        assertEquals(List.of("Java", "AWS", "Kubernetes"), matched);
        assertEquals(List.of("Python", "Docker"), missing);
    }

    @Test
    @DisplayName("12. Composite analyze method returns complete SkillAnalysisResultDto")
    void testCompositeAnalyze() {
        String resumeText = "Experienced Java Developer with Spring Boot, AWS, and MySQL skills.";
        String jobDescription = "Requirements: Java, Spring Boot, AWS, Docker, Kubernetes";

        SkillAnalysisResultDto result = skillEngineService.analyze(resumeText, jobDescription);

        assertNotNull(result);
        assertEquals(List.of("AWS", "Java", "MySQL", "Spring Boot"), result.getDetectedSkills());
        assertEquals(List.of("Java", "Spring Boot", "Docker", "Kubernetes", "AWS"), result.getJobSkills());
        assertEquals(List.of("Java", "Spring Boot", "AWS"), result.getMatchedSkills());
        assertEquals(List.of("Docker", "Kubernetes"), result.getMissingSkills());
    }

    @Test
    @DisplayName("13. Null and empty input safety")
    void testNullAndEmptySafety() {
        assertTrue(skillEngineService.detectResumeSkills(null).isEmpty());
        assertTrue(skillEngineService.detectResumeSkills("").isEmpty());
        assertTrue(skillEngineService.detectResumeSkills("   ").isEmpty());

        assertTrue(skillEngineService.detectJobSkills(null).isEmpty());
        assertTrue(skillEngineService.detectJobSkills("").isEmpty());

        assertTrue(skillEngineService.calculateMatchedSkills(null, null).isEmpty());
        assertTrue(skillEngineService.calculateMissingSkills(null, null).isEmpty());

        SkillAnalysisResultDto emptyResult = skillEngineService.analyze(null, null);
        assertNotNull(emptyResult);
        assertTrue(emptyResult.getDetectedSkills().isEmpty());
        assertTrue(emptyResult.getJobSkills().isEmpty());
    }

    @Test
    @DisplayName("14. Determinism: Repeated runs produce identical results")
    void testDeterminism() {
        String resume = "Python, PyTorch, Machine Learning, Docker, Git";
        String job = "Job: Python, TensorFlow, PyTorch, Docker, Kubernetes";

        SkillAnalysisResultDto r1 = skillEngineService.analyze(resume, job);
        SkillAnalysisResultDto r2 = skillEngineService.analyze(resume, job);

        assertEquals(r1.getDetectedSkills(), r2.getDetectedSkills());
        assertEquals(r1.getJobSkills(), r2.getJobSkills());
        assertEquals(r1.getMatchedSkills(), r2.getMatchedSkills());
        assertEquals(r1.getMissingSkills(), r2.getMissingSkills());
    }
}
