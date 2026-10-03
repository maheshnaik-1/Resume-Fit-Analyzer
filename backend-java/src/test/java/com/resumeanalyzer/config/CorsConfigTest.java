package com.resumeanalyzer.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class CorsConfigTest {

    static class TestCorsRegistry extends CorsRegistry {
        public Map<String, CorsConfiguration> getConfigs() {
            return super.getCorsConfigurations();
        }
    }

    @Test
    @DisplayName("CorsConfig should register mappings for allowed origins and methods without credentials")
    void testCorsConfiguration() {
        CorsConfig corsConfig = new CorsConfig();
        TestCorsRegistry registry = new TestCorsRegistry();
        corsConfig.addCorsMappings(registry);

        Map<String, CorsConfiguration> configs = registry.getConfigs();
        assertNotNull(configs);
        assertTrue(configs.containsKey("/**"));

        CorsConfiguration config = configs.get("/**");
        assertNotNull(config);
        assertEquals(List.of("http://localhost:5173", "http://127.0.0.1:5173"), config.getAllowedOrigins());
        assertEquals(List.of("GET", "POST", "DELETE", "OPTIONS"), config.getAllowedMethods());
        assertEquals(List.of("*"), config.getAllowedHeaders());
        assertNull(config.getAllowCredentials());
    }
}
