package com.resumeanalyzer.config;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:sqlite:target/migration_test.db",
        "spring.datasource.driver-class-name=org.sqlite.JDBC",
        "spring.jpa.database-platform=org.hibernate.community.dialect.SQLiteDialect",
        "spring.jpa.hibernate.ddl-auto=none"
})
public class OpenApiIntegrationTest {

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

    @Test
    @DisplayName("OpenAPI Documentation: /v3/api-docs returns valid JSON with metadata and documented paths")
    void testApiDocsEndpoint() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.openapi", startsWith("3.")))
                .andExpect(jsonPath("$.info.title", is("Resume Fit Analyzer API")))
                .andExpect(jsonPath("$.info.version", is("2.0.0")))
                .andExpect(jsonPath("$.paths['/signup']").exists())
                .andExpect(jsonPath("$.paths['/login']").exists())
                .andExpect(jsonPath("$.paths['/upload']").exists())
                .andExpect(jsonPath("$.paths['/history/{email}']").exists())
                .andExpect(jsonPath("$.paths['/dashboard-stats/{email}']").exists())
                .andExpect(jsonPath("$.paths['/notifications/{email}']").exists())
                .andExpect(jsonPath("$.paths['/']").exists());
    }

    @Test
    @DisplayName("Swagger UI: /swagger-ui.html or redirect is accessible")
    void testSwaggerUiEndpoint() throws Exception {
        mockMvc.perform(get("/swagger-ui.html"))
                .andExpect(status().is3xxRedirection());
    }
}
