package com.resumeanalyzer.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.resumeanalyzer.entity.Notification;
import com.resumeanalyzer.entity.ResumeHistory;
import com.resumeanalyzer.repository.NotificationRepository;
import com.resumeanalyzer.repository.ResumeHistoryRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UploadControllerIntegrationTest {

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
        List<String> testEmails = List.of("candidate@example.com", "fresh_upload_user@example.com", "testuser@example.com");
        for (String email : testEmails) {
            List<ResumeHistory> histories = resumeHistoryRepository.findByEmail(email);
            if (!histories.isEmpty()) {
                resumeHistoryRepository.deleteAll(histories);
            }
            List<Notification> notifs = notificationRepository.findAll().stream()
                    .filter(n -> email.equalsIgnoreCase(n.getEmail()))
                    .toList();
            if (!notifs.isEmpty()) {
                notificationRepository.deleteAll(notifs);
            }
        }
    }

    private byte[] createSamplePdf(String content) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);
            try (PDPageContentStream stream = new PDPageContentStream(document, page)) {
                stream.beginText();
                stream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                stream.newLineAtOffset(50, 700);
                stream.showText(content);
                stream.endText();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            return baos.toByteArray();
        }
    }

    @Test
    @DisplayName("1. Successful upload returns 200 OK with exact 18-key response, notification, and history")
    void testSuccessfulUpload() throws Exception {
        String resumeText = "Experienced Java Developer with Spring Boot, MySQL, REST API, Git, Docker, and OOP. "
                + "Education: B.Tech Computer Science Engineering. "
                + "Certifications: AWS. "
                + "Completed 2 major projects.";
        byte[] pdfBytes = createSamplePdf(resumeText);
        MockMultipartFile file = new MockMultipartFile("file", "resume.pdf", "application/pdf", pdfBytes);

        MvcResult result = mockMvc.perform(multipart("/upload")
                        .file(file)
                        .param("job_description", "Looking for a Java Developer with Spring Boot, MySQL, Docker, and REST API.")
                        .param("email", "candidate@example.com")
                        .param("company", "google")
                        .param("role", "java developer"))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
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

        assertEquals("Google", root.get("company").asText());
        assertEquals("java developer", root.get("role").asText());
        assertEquals("Google", root.get("target_company").asText());

        // Verify database writes
        List<Notification> notifications = notificationRepository.findAll().stream()
                .filter(n -> "candidate@example.com".equalsIgnoreCase(n.getEmail()))
                .toList();
        assertEquals(1, notifications.size());
        assertEquals("candidate@example.com", notifications.get(0).getEmail());
        assertEquals("Resume analyzed for Google - java developer", notifications.get(0).getMessage());
        assertEquals("analysis", notifications.get(0).getType());
        assertEquals(0, notifications.get(0).getIsRead());

        List<ResumeHistory> history = resumeHistoryRepository.findByEmail("candidate@example.com");
        assertEquals(1, history.size());
        assertEquals("candidate@example.com", history.get(0).getEmail());
        assertEquals("Google", history.get(0).getCompany());
        assertEquals("java developer", history.get(0).getRole());
        assertNotNull(history.get(0).getResultJson());
    }

    @Test
    @DisplayName("2. Missing company returns 400 Bad Request")
    void testMissingCompany() throws Exception {
        byte[] pdfBytes = createSamplePdf("Java Developer Resume Content with more than ten characters.");
        MockMultipartFile file = new MockMultipartFile("file", "resume.pdf", "application/pdf", pdfBytes);

        mockMvc.perform(multipart("/upload")
                        .file(file)
                        .param("job_description", "Java")
                        .param("email", "user@example.com")
                        .param("company", "")
                        .param("role", "Developer"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Company selection is required."));
    }

    @Test
    @DisplayName("3. Missing role returns 400 Bad Request")
    void testMissingRole() throws Exception {
        byte[] pdfBytes = createSamplePdf("Java Developer Resume Content with more than ten characters.");
        MockMultipartFile file = new MockMultipartFile("file", "resume.pdf", "application/pdf", pdfBytes);

        mockMvc.perform(multipart("/upload")
                        .file(file)
                        .param("job_description", "Java")
                        .param("email", "user@example.com")
                        .param("company", "Google")
                        .param("role", "   "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Role selection is required."));
    }

    @Test
    @DisplayName("4. Missing email returns 400 Bad Request")
    void testMissingEmail() throws Exception {
        byte[] pdfBytes = createSamplePdf("Java Developer Resume Content with more than ten characters.");
        MockMultipartFile file = new MockMultipartFile("file", "resume.pdf", "application/pdf", pdfBytes);

        mockMvc.perform(multipart("/upload")
                        .file(file)
                        .param("job_description", "Java")
                        .param("email", "")
                        .param("company", "Google")
                        .param("role", "Developer"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("User email is required."));
    }

    @Test
    @DisplayName("5. Non-PDF extension returns 400 Bad Request")
    void testNonPdfExtension() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "resume.txt", "text/plain", "Some dummy text content".getBytes());

        mockMvc.perform(multipart("/upload")
                        .file(file)
                        .param("job_description", "Java")
                        .param("email", "user@example.com")
                        .param("company", "Google")
                        .param("role", "Developer"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Invalid file format. Only PDF documents (.pdf) are supported."));
    }

    @Test
    @DisplayName("6. Empty file returns 400 Bad Request")
    void testEmptyFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "empty.pdf", "application/pdf", new byte[0]);

        mockMvc.perform(multipart("/upload")
                        .file(file)
                        .param("job_description", "Java")
                        .param("email", "user@example.com")
                        .param("company", "Google")
                        .param("role", "Developer"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("The uploaded file is empty. Please upload a valid resume PDF."));
    }

    @Test
    @DisplayName("7. File > 5 MB returns 400 Bad Request")
    void testFileTooLarge() throws Exception {
        byte[] largeBytes = new byte[5 * 1024 * 1024 + 1];
        largeBytes[0] = '%';
        largeBytes[1] = 'P';
        largeBytes[2] = 'D';
        largeBytes[3] = 'F';
        MockMultipartFile file = new MockMultipartFile("file", "large.pdf", "application/pdf", largeBytes);

        mockMvc.perform(multipart("/upload")
                        .file(file)
                        .param("job_description", "Java")
                        .param("email", "user@example.com")
                        .param("company", "Google")
                        .param("role", "Developer"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("File size exceeds the 5 MB limit. Please upload a smaller PDF."));
    }

    @Test
    @DisplayName("8. Invalid PDF header magic bytes returns 400 Bad Request")
    void testInvalidHeader() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "fake.pdf", "application/pdf", "Not a real PDF header".getBytes());

        mockMvc.perform(multipart("/upload")
                        .file(file)
                        .param("job_description", "Java")
                        .param("email", "user@example.com")
                        .param("company", "Google")
                        .param("role", "Developer"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Invalid PDF file. The uploaded file is not a valid PDF document."));
    }

    @Test
    @DisplayName("9. Corrupted PDF returns 400 Bad Request")
    void testCorruptedPdf() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "corrupt.pdf", "application/pdf", "%PDF-corrupted-binary-structure".getBytes());

        mockMvc.perform(multipart("/upload")
                        .file(file)
                        .param("job_description", "Java")
                        .param("email", "user@example.com")
                        .param("company", "Google")
                        .param("role", "Developer"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Unable to parse the PDF file. The file may be corrupted, encrypted, or password-protected."));
    }

    @Test
    @DisplayName("10. Short/unreadable PDF text returns 400 Bad Request")
    void testShortTextPdf() throws Exception {
        byte[] shortPdf = createSamplePdf("Hi");
        MockMultipartFile file = new MockMultipartFile("file", "short.pdf", "application/pdf", shortPdf);

        mockMvc.perform(multipart("/upload")
                        .file(file)
                        .param("job_description", "Java")
                        .param("email", "user@example.com")
                        .param("company", "Google")
                        .param("role", "Developer"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("No readable text found in the PDF. Scanned or image-only documents without an embedded text layer are not supported."));
    }

    @Test
    @DisplayName("11. Empty job description succeeds with zero job skills")
    void testEmptyJobDescription() throws Exception {
        byte[] pdfBytes = createSamplePdf("Experienced Python developer with Django and PostgreSQL.");
        MockMultipartFile file = new MockMultipartFile("file", "resume.pdf", "application/pdf", pdfBytes);

        mockMvc.perform(multipart("/upload")
                        .file(file)
                        .param("job_description", "")
                        .param("email", "user@example.com")
                        .param("company", "Meta")
                        .param("role", "Backend Engineer"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.job_skills").isArray())
                .andExpect(jsonPath("$.job_skills.length()").value(0))
                .andExpect(jsonPath("$.matched_skills.length()").value(0))
                .andExpect(jsonPath("$.score_breakdown.skills").value(0));
    }

    @Test
    @DisplayName("12. Historical best_resume and resume_improvement are queried BEFORE current analysis is saved")
    void testPreSaveHistoricalMetrics() throws Exception {
        byte[] pdfBytes = createSamplePdf("Experienced Java and Spring Boot engineer with MySQL and Docker.");
        MockMultipartFile file = new MockMultipartFile("file", "resume.pdf", "application/pdf", pdfBytes);

        // First upload for this user -> best_resume and resume_improvement should be null
        MvcResult firstResult = mockMvc.perform(multipart("/upload")
                        .file(file)
                        .param("job_description", "Java Spring Boot")
                        .param("email", "fresh_upload_user@example.com")
                        .param("company", "Amazon")
                        .param("role", "SDE-1"))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode firstRoot = objectMapper.readTree(firstResult.getResponse().getContentAsString());
        assertTrue(firstRoot.get("best_resume").isNull());
        assertTrue(firstRoot.get("resume_improvement").isNull());

        // Second upload for this user -> best_resume should now point to upload 1
        MvcResult secondResult = mockMvc.perform(multipart("/upload")
                        .file(file)
                        .param("job_description", "Java Spring Boot MySQL")
                        .param("email", "fresh_upload_user@example.com")
                        .param("company", "Amazon")
                        .param("role", "SDE-1"))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode secondRoot = objectMapper.readTree(secondResult.getResponse().getContentAsString());
        assertFalse(secondRoot.get("best_resume").isNull());
        assertEquals("Amazon", secondRoot.get("best_resume").get("company").asText());
        assertFalse(secondRoot.get("resume_improvement").isNull());
        assertEquals(1, secondRoot.get("resume_improvement").get("history").size());
    }
}
