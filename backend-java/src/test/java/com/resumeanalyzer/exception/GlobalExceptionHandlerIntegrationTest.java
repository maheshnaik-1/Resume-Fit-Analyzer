package com.resumeanalyzer.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.resumeanalyzer.dto.UserLoginRequest;
import com.resumeanalyzer.dto.UserSignupRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:sqlite:target/migration_test.db",
        "spring.datasource.driver-class-name=org.sqlite.JDBC",
        "spring.jpa.database-platform=org.hibernate.community.dialect.SQLiteDialect",
        "spring.jpa.hibernate.ddl-auto=none"
})
public class GlobalExceptionHandlerIntegrationTest {

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
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Signup Validation Failure: Blank name returns HTTP 400 with detail and errors map")
    void testSignupValidationBlankName() throws Exception {
        UserSignupRequest request = new UserSignupRequest("", "validuser@example.com", "Password123!");

        mockMvc.perform(post("/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail", not(emptyOrNullString())))
                .andExpect(jsonPath("$.message", is("Validation failed")))
                .andExpect(jsonPath("$.errors.name", is("Name is required")));
    }

    @Test
    @DisplayName("Signup Validation Failure: Invalid email returns HTTP 400 with detail and errors map")
    void testSignupValidationInvalidEmail() throws Exception {
        UserSignupRequest request = new UserSignupRequest("John Doe", "not-a-valid-email", "Password123!");

        mockMvc.perform(post("/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail", not(emptyOrNullString())))
                .andExpect(jsonPath("$.message", is("Validation failed")))
                .andExpect(jsonPath("$.errors.email", is("Invalid email format")));
    }

    @Test
    @DisplayName("Signup Validation Failure: Short password returns HTTP 400")
    void testSignupValidationShortPassword() throws Exception {
        UserSignupRequest request = new UserSignupRequest("John Doe", "validuser@example.com", "123");

        mockMvc.perform(post("/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail", not(emptyOrNullString())))
                .andExpect(jsonPath("$.message", is("Validation failed")))
                .andExpect(jsonPath("$.errors.password", is("Password must be at least 6 characters")));
    }

    @Test
    @DisplayName("Login Validation Failure: Blank email and password return HTTP 400 with multiple field errors")
    void testLoginValidationBlankFields() throws Exception {
        UserLoginRequest request = new UserLoginRequest("", "");

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail", not(emptyOrNullString())))
                .andExpect(jsonPath("$.message", is("Validation failed")))
                .andExpect(jsonPath("$.errors.email", not(emptyOrNullString())))
                .andExpect(jsonPath("$.errors.password", is("Password is required")));
    }

    @Test
    @DisplayName("Malformed JSON Body: Returns HTTP 400 with clean error detail")
    void testMalformedJsonBody() throws Exception {
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{invalid-json-body"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail", is("Malformed or missing JSON request body.")));
    }

    @Test
    @DisplayName("ResourceNotFoundException Handling: Nonexistent history result returns HTTP 404")
    void testResourceNotFoundHandling() throws Exception {
        mockMvc.perform(get("/history/result/999999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail", is("History record not found")));
    }
}
