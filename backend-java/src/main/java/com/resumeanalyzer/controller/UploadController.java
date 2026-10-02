package com.resumeanalyzer.controller;

import com.resumeanalyzer.dto.AnalysisResponseDto;
import com.resumeanalyzer.dto.ErrorDetailResponse;
import com.resumeanalyzer.exception.InvalidFileException;
import com.resumeanalyzer.exception.PdfParsingException;
import com.resumeanalyzer.service.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

@RestController
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
    public ResponseEntity<AnalysisResponseDto> uploadResume(
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "job_description", required = false, defaultValue = "") String jobDescription,
            @RequestParam(value = "email", required = false, defaultValue = "") String email,
            @RequestParam(value = "company", required = false, defaultValue = "") String company,
            @RequestParam(value = "role", required = false, defaultValue = "") String role
    ) {
        AnalysisResponseDto response = uploadService.processUpload(file, jobDescription, email, company, role);
        return ResponseEntity.ok(response);
    }

    @ExceptionHandler(InvalidFileException.class)
    public ResponseEntity<ErrorDetailResponse> handleInvalidFile(InvalidFileException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorDetailResponse(ex.getMessage()));
    }

    @ExceptionHandler(PdfParsingException.class)
    public ResponseEntity<ErrorDetailResponse> handlePdfParsing(PdfParsingException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorDetailResponse(ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorDetailResponse> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorDetailResponse(ex.getMessage()));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorDetailResponse> handleMaxUploadSize(MaxUploadSizeExceededException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorDetailResponse("File size exceeds the 5 MB limit. Please upload a smaller PDF."));
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ErrorDetailResponse> handleMissingPart(MissingServletRequestPartException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorDetailResponse("The uploaded file is empty. Please upload a valid resume PDF."));
    }
}
