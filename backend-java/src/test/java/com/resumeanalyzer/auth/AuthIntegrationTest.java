package com.resumeanalyzer.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.resumeanalyzer.dto.UserLoginRequest;
import com.resumeanalyzer.dto.UserSignupRequest;
import com.resumeanalyzer.entity.User;
import com.resumeanalyzer.repository.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:sqlite:target/migration_test.db",
        "spring.datasource.driver-class-name=org.sqlite.JDBC",
        "spring.jpa.database-platform=org.hibernate.community.dialect.SQLiteDialect",
        "spring.jpa.hibernate.ddl-auto=none"
})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthIntegrationTest {

    @BeforeAll
    static void ensureTestDatabaseExists() throws IOException {
        Path targetDb = Path.of("target", "migration_test.db");
        Path sourceDb = Path.of("..", "backend", "resume_analyzer.db");
        Files.createDirectories(targetDb.getParent());
        if (Files.exists(sourceDb)) {
            Files.copy(sourceDb, targetDb, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @Order(1)
    @DisplayName("Verify BCrypt compatibility: Java PasswordEncoder can verify standard BCrypt hashes")
    void testBcryptCompatibility() {
        String rawPassword = "TestVerificationPassword99!";
        String encoded = passwordEncoder.encode(rawPassword);
        assertTrue(encoded.startsWith("$2a$") || encoded.startsWith("$2b$"),
                "Encoded password must follow standard modular crypt format");
        assertTrue(passwordEncoder.matches(rawPassword, encoded),
                "Encoder must match correct password");
        assertFalse(passwordEncoder.matches("WrongPassword", encoded),
                "Encoder must reject incorrect password");
    }

    @Test
    @Order(2)
    @DisplayName("Verify Login: Unknown email returns HTTP 200 with success=false, message='User not found'")
    void testLoginUnknownEmail() throws Exception {
        UserLoginRequest request = new UserLoginRequest("nonexistent_user@example.com", "any_password");

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("User not found")));
    }

    @Test
    @Order(3)
    @DisplayName("Verify Login: Existing user with incorrect password returns HTTP 200 with success=false, message='Incorrect password'")
    void testLoginIncorrectPassword() throws Exception {
        UserLoginRequest request = new UserLoginRequest("boii@gmail.com", "DefinitelyWrongPassword123");

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Incorrect password")));
    }

    @Test
    @Order(4)
    @DisplayName("Verify Signup: Creates new user with normalized email and BCrypt hashed password")
    void testSignupNewUser() throws Exception {
        String testEmailInput = "   NewUser_V15@Example.COM   ";
        String testPassword = "StrongSecurePassword2026!";
        UserSignupRequest request = new UserSignupRequest("New V15 User", testEmailInput, testPassword);

        mockMvc.perform(post("/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("User registered successfully")));

        // Verify database storage
        Optional<User> savedUserOpt = userRepository.findByNormalizedEmail("newuser_v15@example.com");
        assertTrue(savedUserOpt.isPresent(), "User should be saved with normalized email");

        User savedUser = savedUserOpt.get();
        assertEquals("New V15 User", savedUser.getName());
        assertEquals("newuser_v15@example.com", savedUser.getEmail(), "Email must be stored lowercase and trimmed");
        assertNotEquals(testPassword, savedUser.getPassword(), "Plaintext password must NOT be stored");
        assertTrue(savedUser.getPassword().startsWith("$2a$") || savedUser.getPassword().startsWith("$2b$"),
                "Password must be stored as BCrypt hash");
        assertTrue(passwordEncoder.matches(testPassword, savedUser.getPassword()),
                "Saved BCrypt hash must match plaintext password");
    }

    @Test
    @Order(5)
    @DisplayName("Verify Login: Successful login with newly created user")
    void testLoginSuccessful() throws Exception {
        UserLoginRequest request = new UserLoginRequest("newuser_v15@example.com", "StrongSecurePassword2026!");

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Login successful")))
                .andExpect(jsonPath("$.name", is("New V15 User")))
                .andExpect(jsonPath("$.email", is("newuser_v15@example.com")));
    }

    @Test
    @Order(6)
    @DisplayName("Verify Login: Email normalization (mixed case and whitespace) on login")
    void testLoginEmailNormalization() throws Exception {
        UserLoginRequest request = new UserLoginRequest("   NEWUSER_V15@EXAMPLE.COM   ", "StrongSecurePassword2026!");

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Login successful")))
                .andExpect(jsonPath("$.name", is("New V15 User")))
                .andExpect(jsonPath("$.email", is("newuser_v15@example.com")));
    }

    @Test
    @Order(7)
    @DisplayName("Verify Duplicate Signup: Returns HTTP 400 with detail='Email already exists'")
    void testDuplicateSignup() throws Exception {
        UserSignupRequest request = new UserSignupRequest("Duplicate User", "newuser_v15@example.com", "AnotherPassword123!");

        mockMvc.perform(post("/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail", is("Email already exists")));
    }

    @Test
    @Order(8)
    @DisplayName("Verify Duplicate Signup: Case-insensitive and whitespace-insensitive duplicate check")
    void testDuplicateSignupCaseAndWhitespace() throws Exception {
        UserSignupRequest request = new UserSignupRequest("Duplicate User", "  NEWUSER_V15@example.com  ", "AnotherPassword123!");

        mockMvc.perform(post("/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail", is("Email already exists")));
    }
}
