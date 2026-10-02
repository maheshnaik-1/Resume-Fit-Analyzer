package com.resumeanalyzer.repository;

import com.resumeanalyzer.entity.Notification;
import com.resumeanalyzer.entity.ResumeHistory;
import com.resumeanalyzer.entity.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:sqlite:target/migration_test.db",
        "spring.datasource.driver-class-name=org.sqlite.JDBC",
        "spring.jpa.database-platform=org.hibernate.community.dialect.SQLiteDialect",
        "spring.jpa.hibernate.ddl-auto=none"
})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RepositoryIntegrationTest {

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
    private UserRepository userRepository;

    @Autowired
    private ResumeHistoryRepository resumeHistoryRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Test
    @Order(1)
    @DisplayName("Verify UserRepository normalized email lookup and existence")
    void testUserRepositoryNormalizedEmailLookup() {
        // Test with uppercase and surrounding whitespace
        String queryEmail = "   BoIi@GmAiL.cOm  ";
        Optional<User> userOptional = userRepository.findByNormalizedEmail(queryEmail);

        assertTrue(userOptional.isPresent(), "User should be found with normalized email");
        User user = userOptional.get();
        assertEquals("boii@gmail.com", user.getEmail(), "Stored email must match canonical address");
        assertEquals("Mahesh", user.getName(), "User name must match record");
        assertNotNull(user.getPassword(), "Password hash must be present");
        assertTrue(user.getPassword().startsWith("$2b$"), "Password hash format should be bcrypt");

        // Test existence check
        boolean exists = userRepository.existsByNormalizedEmail("  BOII@GMAIL.COM ");
        assertTrue(exists, "existsByNormalizedEmail should return true for existing user");

        boolean nonExistent = userRepository.existsByNormalizedEmail("nonexistent_user@example.com");
        assertFalse(nonExistent, "existsByNormalizedEmail should return false for nonexistent email");
    }

    @Test
    @Order(2)
    @DisplayName("Verify ResumeHistoryRepository history retrieval and ordering")
    void testResumeHistoryRetrievalAndOrdering() {
        String email = "boii@gmail.com";
        List<ResumeHistory> history = resumeHistoryRepository.findByEmailOrderByAnalyzedAtDesc(email);

        assertNotNull(history, "History list must not be null");
        assertFalse(history.isEmpty(), "History list must contain records");
        assertTrue(history.size() >= 66, "Should match total history records for test user");

        // Verify descending chronological ordering
        for (int i = 0; i < history.size() - 1; i++) {
            String current = history.get(i).getAnalyzedAt();
            String next = history.get(i + 1).getAnalyzedAt();
            if (current != null && next != null) {
                assertTrue(current.compareTo(next) >= 0,
                        "History must be ordered by analyzed_at DESC: " + current + " >= " + next);
            }
        }
    }

    @Test
    @Order(3)
    @DisplayName("Verify ResumeHistoryRepository lookup by ID and result_json payload")
    void testResumeHistoryLookupById() {
        String email = "boii@gmail.com";
        List<ResumeHistory> history = resumeHistoryRepository.findByEmailOrderByAnalyzedAtDesc(email);
        assertFalse(history.isEmpty());

        ResumeHistory latest = history.get(0);
        Optional<ResumeHistory> fetchedOpt = resumeHistoryRepository.findById(latest.getId());

        assertTrue(fetchedOpt.isPresent(), "Record must be found by ID");
        ResumeHistory fetched = fetchedOpt.get();
        assertEquals(latest.getId(), fetched.getId());
        assertNotNull(fetched.getResultJson(), "result_json must be present as a String");
        assertTrue(fetched.getResultJson().contains("ats_score"), "result_json must contain expected analysis keys");
    }

    @Test
    @Order(4)
    @DisplayName("Verify ResumeHistoryRepository findFirstByEmailOrderByAtsScoreDesc")
    void testBestResumeRecord() {
        String email = "boii@gmail.com";
        Optional<ResumeHistory> bestOpt = resumeHistoryRepository.findFirstByEmailOrderByAtsScoreDesc(email);

        assertTrue(bestOpt.isPresent(), "Best resume record must be found");
        ResumeHistory best = bestOpt.get();
        assertNotNull(best.getAtsScore(), "ATS score must be present");

        // Confirm this ATS score is >= all other scores for this email
        List<ResumeHistory> allHistory = resumeHistoryRepository.findByEmail(email);
        for (ResumeHistory r : allHistory) {
            if (r.getAtsScore() != null) {
                assertTrue(best.getAtsScore() >= r.getAtsScore(),
                        "Best ATS score (" + best.getAtsScore() + ") must be >= other score (" + r.getAtsScore() + ")");
            }
        }
    }

    @Test
    @Order(5)
    @DisplayName("Verify ResumeHistoryRepository findTop3ImprovementHistory with case-insensitivity and whitespace")
    void testImprovementQuery() {
        String email = "boii@gmail.com";
        // 'Tcs' + 'Java Developer' has 8 records in test DB
        String queryCompany = "  tCs  ";
        String queryRole = "  java DEVELOPER  ";

        List<ResumeHistory> top3 = resumeHistoryRepository.findTop3ImprovementHistory(email, queryCompany, queryRole);

        assertNotNull(top3, "Improvement history must not be null");
        assertEquals(3, top3.size(), "findTop3ImprovementHistory must return at most 3 records");

        // Verify ordering is DESC
        for (int i = 0; i < top3.size() - 1; i++) {
            String current = top3.get(i).getAnalyzedAt();
            String next = top3.get(i + 1).getAnalyzedAt();
            if (current != null && next != null) {
                assertTrue(current.compareTo(next) >= 0,
                        "Improvement records must be ordered by analyzed_at DESC");
            }
        }

        // Verify company and role match case-insensitively
        for (ResumeHistory r : top3) {
            assertEquals("Tcs", r.getCompany().trim());
            assertEquals("Java Developer", r.getRole().trim());
        }
    }

    @Test
    @Order(6)
    @DisplayName("Verify NotificationRepository retrieval (LIMIT 10) and markAllAsReadByEmail")
    void testNotificationRetrievalAndMarkAsRead() {
        String email = "boii@gmail.com";

        // Step A: Retrieve notifications (LIMIT 10, DESC)
        List<Notification> notifications = notificationRepository.findTop10ByEmailOrderByCreatedAtDesc(email);
        assertNotNull(notifications);
        assertEquals(10, notifications.size(), "Must return top 10 notifications");

        for (int i = 0; i < notifications.size() - 1; i++) {
            String current = notifications.get(i).getCreatedAt();
            String next = notifications.get(i + 1).getCreatedAt();
            if (current != null && next != null) {
                assertTrue(current.compareTo(next) >= 0,
                        "Notifications must be ordered by created_at DESC");
            }
        }

        // Step B: Mark notifications as read in the isolated test database
        int updated = notificationRepository.markAllAsReadByEmail(email);
        assertTrue(updated > 0, "markAllAsReadByEmail should update affected rows");

        // Step C: Verify updated notifications have isRead == 1
        List<Notification> updatedList = notificationRepository.findTop10ByEmailOrderByCreatedAtDesc(email);
        for (Notification n : updatedList) {
            assertEquals(1, n.getIsRead(), "Notification is_read must be updated to 1");
        }
    }
}
