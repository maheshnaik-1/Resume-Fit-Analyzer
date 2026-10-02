package com.resumeanalyzer.database;

import com.resumeanalyzer.entity.Notification;
import com.resumeanalyzer.entity.ResumeHistory;
import com.resumeanalyzer.entity.User;
import com.resumeanalyzer.repository.NotificationRepository;
import com.resumeanalyzer.repository.ResumeHistoryRepository;
import com.resumeanalyzer.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class DatabaseIntegrationTest {

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
    private EntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ResumeHistoryRepository resumeHistoryRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Test
    @DisplayName("Verify EntityManager and ApplicationContext start successfully")
    void testApplicationContextAndEntityManager() {
        assertNotNull(entityManager, "EntityManager must be injected");
        assertTrue(entityManager.isOpen(), "EntityManager must be open");
    }

    @Test
    @DisplayName("Verify UserRepository can read existing user in read-only mode")
    void testUserRepositoryRead() {
        Optional<User> userOpt = userRepository.findByNormalizedEmail("boii@gmail.com");
        assertTrue(userOpt.isPresent(), "Existing user boii@gmail.com must be readable");
        User user = userOpt.get();
        assertEquals("Mahesh", user.getName());
        assertEquals("boii@gmail.com", user.getEmail());
        assertNotNull(user.getPassword());
    }

    @Test
    @DisplayName("Verify ResumeHistoryRepository can read existing history in read-only mode")
    void testResumeHistoryRepositoryRead() {
        List<ResumeHistory> history = resumeHistoryRepository.findByEmailOrderByAnalyzedAtDesc("boii@gmail.com");
        assertNotNull(history);
        assertEquals(66, history.size(), "Should match total existing history records");
        ResumeHistory latest = history.get(0);
        assertNotNull(latest.getCompany());
        assertNotNull(latest.getRole());
        assertNotNull(latest.getAtsScore());
        assertNotNull(latest.getResultJson());
    }

    @Test
    @DisplayName("Verify NotificationRepository can read existing notifications in read-only mode")
    void testNotificationRepositoryRead() {
        List<Notification> notifications = notificationRepository.findTop10ByEmailOrderByCreatedAtDesc("boii@gmail.com");
        assertNotNull(notifications);
        assertEquals(10, notifications.size(), "Should return exactly top 10 notifications");
        Notification latest = notifications.get(0);
        assertEquals("boii@gmail.com", latest.getEmail());
        assertNotNull(latest.getMessage());
    }

    @Test
    @DisplayName("Verify SQLite schema tables remain intact without alterations")
    void testSchemaIntegrity() {
        @SuppressWarnings("unchecked")
        List<String> tables = entityManager.createNativeQuery(
                "SELECT name FROM sqlite_master WHERE type='table' AND name IN ('users', 'resume_history', 'notifications') ORDER BY name"
        ).getResultList();

        assertEquals(3, tables.size(), "All 3 core tables must be present in SQLite");
        assertTrue(tables.contains("users"));
        assertTrue(tables.contains("resume_history"));
        assertTrue(tables.contains("notifications"));
    }
}
