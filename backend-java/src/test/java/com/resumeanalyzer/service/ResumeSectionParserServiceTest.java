package com.resumeanalyzer.service;

import com.resumeanalyzer.dto.ResumeSummaryDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ResumeSectionParserServiceTest {

    private ResumeSectionParserService parserService;

    @BeforeEach
    void setUp() {
        parserService = new ResumeSectionParserService();
    }

    // ==========================================
    // 1. Education Extraction Tests
    // ==========================================

    @Test
    @DisplayName("Education: B.Tech and Bachelor of Technology matching")
    void testEducationBTech() {
        List<String> r1 = parserService.extractEducation("Education: B.Tech in Computer Science");
        assertTrue(r1.contains("B.Tech"));
        assertTrue(r1.contains("Computer Science"));

        List<String> r2 = parserService.extractEducation("Degree: Bachelor of Technology from ABC University");
        assertTrue(r2.contains("B.Tech"));

        List<String> r3 = parserService.extractEducation("Completed b.tech in 2024");
        assertTrue(r3.contains("B.Tech"));
    }

    @Test
    @DisplayName("Education: M.Tech matching")
    void testEducationMTech() {
        List<String> r = parserService.extractEducation("Education: M.Tech in CSE");
        assertTrue(r.contains("M.Tech"));
        assertTrue(r.contains("Computer Science Engineering"));
    }

    @Test
    @DisplayName("Education: BCA and MCA matching")
    void testEducationBcaMca() {
        List<String> r1 = parserService.extractEducation("Candidate holds a BCA degree.");
        assertTrue(r1.contains("BCA"));

        List<String> r2 = parserService.extractEducation("Candidate holds an MCA in Information Technology.");
        assertTrue(r2.contains("MCA"));
        assertTrue(r2.contains("Information Technology"));
    }

    @Test
    @DisplayName("Education: B.Sc and Diploma matching")
    void testEducationBScDiploma() {
        List<String> r1 = parserService.extractEducation("Degree: B.Sc in Computer Science");
        assertTrue(r1.contains("B.Sc"));
        assertTrue(r1.contains("Computer Science"));

        List<String> r2 = parserService.extractEducation("Qualifications: Diploma in IT");
        assertTrue(r2.contains("Diploma"));
        assertTrue(r2.contains("Information Technology"));
    }

    @Test
    @DisplayName("Education: Branch matching (CSE, Computer Science, IT)")
    void testEducationBranches() {
        List<String> r1 = parserService.extractEducation("Specialization in CSE");
        assertTrue(r1.contains("Computer Science Engineering"));

        List<String> r2 = parserService.extractEducation("Degree in Computer Science Engineering");
        assertTrue(r2.contains("Computer Science Engineering"));

        List<String> r3 = parserService.extractEducation("Major: Computer Science");
        assertTrue(r3.contains("Computer Science"));

        List<String> r4 = parserService.extractEducation("Stream: IT");
        assertTrue(r4.contains("Information Technology"));

        List<String> r5 = parserService.extractEducation("Department of Information Technology");
        assertTrue(r5.contains("Information Technology"));
    }

    @Test
    @DisplayName("Education: Empty or null text returns empty list")
    void testEducationEmptyText() {
        assertTrue(parserService.extractEducation(null).isEmpty());
        assertTrue(parserService.extractEducation("").isEmpty());
        assertTrue(parserService.extractEducation("   ").isEmpty());
    }

    // ==========================================
    // 2. Certification Extraction Tests
    // ==========================================

    @Test
    @DisplayName("Certifications: Case-insensitive matching and canonical output names")
    void testCertificationsMatching() {
        String text = "Certifications: AWS Certified Developer, proficient in PYTHON PROGRAMMING and Machine Learning.";
        List<String> certs = parserService.extractCertifications(text);

        assertEquals(3, certs.size());
        assertTrue(certs.contains("AWS"));
        assertTrue(certs.contains("Python Programming"));
        assertTrue(certs.contains("Machine Learning"));
    }

    @Test
    @DisplayName("Certifications: Generative AI, Azure, Google Cloud, Data Science, Java Programming")
    void testAllCertifications() {
        String text = "Certified in Java Programming, Generative AI, azure, GOOGLE CLOUD, and data science.";
        List<String> certs = parserService.extractCertifications(text);

        assertEquals(5, certs.size());
        assertTrue(certs.contains("Java Programming"));
        assertTrue(certs.contains("Generative AI"));
        assertTrue(certs.contains("Azure"));
        assertTrue(certs.contains("Google Cloud"));
        assertTrue(certs.contains("Data Science"));
    }

    @Test
    @DisplayName("Certifications: Unrelated text produces no false positives")
    void testCertificationsNoFalsePositives() {
        String text = "Experienced software developer with strong problem solving and communication skills.";
        List<String> certs = parserService.extractCertifications(text);
        assertTrue(certs.isEmpty());
    }

    // ==========================================
    // 3. Project Extraction Tests
    // ==========================================

    @Test
    @DisplayName("Projects: Zero projects detected")
    void testProjectsZero() {
        String text = "Experienced developer working on enterprise applications.";
        List<String> projects = parserService.extractProjects(text);
        assertTrue(projects.isEmpty());
    }

    @Test
    @DisplayName("Projects: One project detected")
    void testProjectsOne() {
        String text = "Completed a single project on ecommerce.";
        List<String> projects = parserService.extractProjects(text);

        assertEquals(1, projects.size());
        assertEquals("1 Project Detected", projects.get(0));
    }

    @Test
    @DisplayName("Projects: Two projects detected")
    void testProjectsTwo() {
        // "major project" matches both "project" (1) and "major project" (1) -> count 2
        String text = "Completed a major project on ecommerce.";
        List<String> projects = parserService.extractProjects(text);

        assertEquals(1, projects.size());
        assertEquals("2 Projects Detected", projects.get(0));
    }

    @Test
    @DisplayName("Projects: More than three project keyword matches clamped to 3")
    void testProjectsClampedToThree() {
        String text = "Project 1: mini project. Project 2: major project. Project 3: personal project. Academic project 4.";
        List<String> projects = parserService.extractProjects(text);

        assertEquals(1, projects.size());
        assertEquals("3 Projects Detected", projects.get(0));
    }

    // ==========================================
    // 4. Composite & Determinism Tests
    // ==========================================

    @Test
    @DisplayName("Composite parseResumeSections returns structured ResumeSummaryDto")
    void testCompositeParsing() {
        String text = "B.Tech in Computer Science Engineering. Certified in AWS. Completed a single project.";
        ResumeSummaryDto dto = parserService.parseResumeSections(text);

        assertNotNull(dto);
        assertEquals(List.of("B.Tech", "Computer Science Engineering"), dto.getEducation());
        assertEquals(List.of("AWS"), dto.getCertifications());
        assertEquals(List.of("1 Project Detected"), dto.getProjects());
    }

    @Test
    @DisplayName("Determinism: Multiple calls with identical input return identical output")
    void testParsingDeterminism() {
        String text = "MCA graduate in Information Technology with Python Programming certification and personal project experience.";

        ResumeSummaryDto dto1 = parserService.parseResumeSections(text);
        ResumeSummaryDto dto2 = parserService.parseResumeSections(text);

        assertEquals(dto1.getEducation(), dto2.getEducation());
        assertEquals(dto1.getCertifications(), dto2.getCertifications());
        assertEquals(dto1.getProjects(), dto2.getProjects());
    }
}
