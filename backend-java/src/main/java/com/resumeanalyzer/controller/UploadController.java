package com.resumeanalyzer.controller;

import com.resumeanalyzer.dto.AnalysisResponseDto;
import com.resumeanalyzer.service.UploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Tag(name = "Resume Analysis", description = "Endpoints for uploading and analyzing resumes against target job profiles")
public class UploadController {

    private final UploadService uploadService;

    @Autowired
    public UploadController(UploadService uploadService) {
        this.uploadService = uploadService;
    }

    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @Operation(
            summary = "Upload and analyze resume",
            description = "Extracts text from uploaded PDF, scores against company and role requirements, and computes ATS metrics."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Resume analyzed successfully",
                    content = @Content(schema = @Schema(implementation = AnalysisResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation failure, corrupt PDF, or missing required fields"
            )
    })
    public ResponseEntity<AnalysisResponseDto> uploadResume(
            @Parameter(description = "Resume PDF file to analyze (max 5 MB)", required = true)
            @RequestParam(value = "file", required = false) MultipartFile file,
            @Parameter(description = "Optional target job description text")
            @RequestParam(value = "job_description", required = false, defaultValue = "") String jobDescription,
            @Parameter(description = "User account email address", required = true)
            @RequestParam(value = "email", required = false, defaultValue = "") String email,
            @Parameter(description = "Target company name", required = true)
            @RequestParam(value = "company", required = false, defaultValue = "") String company,
            @Parameter(description = "Target job role name", required = true)
            @RequestParam(value = "role", required = false, defaultValue = "") String role
    ) {
        AnalysisResponseDto response = uploadService.processUpload(file, jobDescription, email, company, role);
        return ResponseEntity.ok(response);
    }
}

