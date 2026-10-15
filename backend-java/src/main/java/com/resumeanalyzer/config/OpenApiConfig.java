package com.resumeanalyzer.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI resumeAnalyzerOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Resume Fit Analyzer API")
                        .version("2.0.0")
                        .description("REST API documentation for Resume Fit Analyzer - automated ATS scoring, role matching, and resume improvement insights.")
                        .contact(new Contact()
                                .name("Resume Analyzer Engineering Team")
                                .email("support@resumeanalyzer.com"))
                        .license(new License()
                                .name("Proprietary")
                                .url("https://resumeanalyzer.com/terms")));
    }
}
