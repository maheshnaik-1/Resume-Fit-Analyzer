package com.resumeanalyzer.controller;

import com.resumeanalyzer.entity.Notification;
import com.resumeanalyzer.repository.NotificationRepository;
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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class NotificationControllerIntegrationTest {

    private static final String TEST_EMAIL = "notification_test_user@example.com";

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
    private NotificationRepository notificationRepository;

    @BeforeEach
    void setUp() {
        cleanTestData();
    }

    @AfterEach
    void tearDown() {
        cleanTestData();
    }

    private void cleanTestData() {
        List<Notification> notifs = notificationRepository.findAll().stream()
                .filter(n -> TEST_EMAIL.equalsIgnoreCase(n.getEmail()))
                .toList();
        if (!notifs.isEmpty()) {
            notificationRepository.deleteAll(notifs);
        }
    }

    @Test
    @DisplayName("GET /notifications/{email} - Should return notifications ordered by created_at DESC with max 10 limit")
    void testGetNotificationsMax10AndOrdering() throws Exception {
        List<Notification> batch = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            String timestamp = String.format("2026-10-%02d 10:00:00", i);
            batch.add(new Notification(TEST_EMAIL, "Notification " + i, "analysis", 0, timestamp));
        }
        notificationRepository.saveAll(batch);

        mockMvc.perform(get("/notifications/" + TEST_EMAIL))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(10)))
                .andExpect(jsonPath("$[0].message", is("Notification 12")))
                .andExpect(jsonPath("$[0].created_at", is("2026-10-12 10:00:00")))
                .andExpect(jsonPath("$[0].type", is("analysis")))
                .andExpect(jsonPath("$[0].is_read", is(0)))
                .andExpect(jsonPath("$[0].id").doesNotExist())
                .andExpect(jsonPath("$[9].message", is("Notification 3")));
    }

    @Test
    @DisplayName("GET /notifications/{email} - Should return empty array when user has no notifications")
    void testGetNotificationsEmpty() throws Exception {
        mockMvc.perform(get("/notifications/nonexistent_notif_user@example.com"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /notifications/{email} - Should normalize email case and whitespace")
    void testGetNotificationsNormalizedEmail() throws Exception {
        Notification n = new Notification(TEST_EMAIL, "Welcome!", "welcome", 0, "2026-10-01 10:00:00");
        notificationRepository.save(n);

        mockMvc.perform(get("/notifications/  NOTIFICATION_TEST_USER@EXAMPLE.COM  "))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].message", is("Welcome!")));
    }

    @Test
    @DisplayName("POST /notifications/read/{email} - Should mark all notifications as read")
    void testMarkNotificationsAsRead() throws Exception {
        Notification n1 = new Notification(TEST_EMAIL, "Alert 1", "analysis", 0, "2026-10-01 10:00:00");
        Notification n2 = new Notification(TEST_EMAIL, "Alert 2", "analysis", 0, "2026-10-02 10:00:00");
        notificationRepository.saveAll(List.of(n1, n2));

        mockMvc.perform(post("/notifications/read/" + TEST_EMAIL))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Notifications marked as read")));

        List<Notification> updated = notificationRepository.findAll().stream()
                .filter(n -> TEST_EMAIL.equalsIgnoreCase(n.getEmail()))
                .toList();
        assertEquals(2, updated.size());
        for (Notification notif : updated) {
            assertEquals(1, notif.getIsRead());
        }
    }

    @Test
    @DisplayName("POST /notifications/read/{email} - Should succeed even when user has zero notifications")
    void testMarkNotificationsAsReadZeroNotifications() throws Exception {
        mockMvc.perform(post("/notifications/read/nonexistent_zero_notif_user@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Notifications marked as read")));
    }
}
