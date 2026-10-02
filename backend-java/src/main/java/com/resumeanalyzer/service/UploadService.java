package com.resumeanalyzer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.resumeanalyzer.dto.AnalysisResponseDto;
import com.resumeanalyzer.dto.BestResumeDto;
import com.resumeanalyzer.dto.ResumeImprovementDto;
import com.resumeanalyzer.entity.Notification;
import com.resumeanalyzer.entity.ResumeHistory;
import com.resumeanalyzer.exception.InvalidFileException;
import com.resumeanalyzer.repository.NotificationRepository;
import com.resumeanalyzer.repository.ResumeHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class UploadService {

    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final PdfExtractorService pdfExtractorService;
    private final AnalysisAssemblyService analysisAssemblyService;
    private final ResumeImprovementService resumeImprovementService;
    private final ResumeHistoryRepository resumeHistoryRepository;
    private final NotificationRepository notificationRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public UploadService(
            PdfExtractorService pdfExtractorService,
            AnalysisAssemblyService analysisAssemblyService,
            ResumeImprovementService resumeImprovementService,
            ResumeHistoryRepository resumeHistoryRepository,
            NotificationRepository notificationRepository,
            ObjectMapper objectMapper
    ) {
        this.pdfExtractorService = pdfExtractorService;
        this.analysisAssemblyService = analysisAssemblyService;
        this.resumeImprovementService = resumeImprovementService;
        this.resumeHistoryRepository = resumeHistoryRepository;
        this.notificationRepository = notificationRepository;
        this.objectMapper = objectMapper;
    }

    public AnalysisResponseDto processUpload(
            MultipartFile file,
            String jobDescription,
            String email,
            String company,
            String role
    ) {
        // 1. Validate Form Fields (Exact Python V1.0 order: company -> role -> email)
        if (company == null || company.trim().isEmpty()) {
            throw new InvalidFileException("Company selection is required.");
        }
        if (role == null || role.trim().isEmpty()) {
            throw new InvalidFileException("Role selection is required.");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new InvalidFileException("User email is required.");
        }

        String cleanCompany = company.trim();
        String cleanRole = role.trim();
        String cleanEmail = email.trim().toLowerCase();
        String displayCompany = toTitleCase(cleanCompany);
        String jd = jobDescription != null ? jobDescription : "";

        // 2. Extract and Validate PDF Text (Timer starts as in Python V1.0)
        long startTime = System.nanoTime();
        String resumeText = pdfExtractorService.extractText(file);

        String nowFormatted = LocalDateTime.now().format(TIMESTAMP_FORMATTER);

        // 3. Save Notification (Before timer stops, exactly matching Python V1.0 line 767)
        Notification notification = new Notification(
                cleanEmail,
                "Resume analyzed for " + displayCompany + " - " + cleanRole,
                "analysis",
                0,
                nowFormatted
        );
        notificationRepository.save(notification);

        // 4. Calculate Execution Timing (round to 2 decimal places)
        double elapsedSeconds = (System.nanoTime() - startTime) / 1_000_000_000.0;
        double analysisTime = BigDecimal.valueOf(elapsedSeconds)
                .setScale(2, RoundingMode.HALF_EVEN)
                .doubleValue();

        // 5. Query Historical Best Resume and Improvement BEFORE saving current analysis (Exact Python V1.0 parity)
        Optional<ResumeHistory> bestHistory = resumeHistoryRepository.findFirstByEmailOrderByAtsScoreDesc(cleanEmail);
        BestResumeDto bestResume = bestHistory.map(h -> new BestResumeDto(
                h.getCompany(),
                h.getRole(),
                h.getAtsScore(),
                h.getAnalyzedAt()
        )).orElse(null);

        List<ResumeHistory> top3Recent = resumeHistoryRepository.findTop3ImprovementHistory(
                cleanEmail,
                cleanCompany,
                cleanRole
        );
        ResumeImprovementDto resumeImprovement = resumeImprovementService.calculateResumeImprovement(top3Recent);

        // 6. Assemble Analysis Response DTO
        String originalFilename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "";
        AnalysisResponseDto response = analysisAssemblyService.assembleAnalysis(
                originalFilename,
                resumeText,
                jd,
                cleanCompany,
                cleanRole,
                analysisTime,
                bestResume,
                resumeImprovement
        );

        // 7. Serialize Result JSON and Save Resume History
        String resultJson;
        try {
            resultJson = objectMapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            resultJson = "{}";
        }

        ResumeHistory history = new ResumeHistory(
                cleanEmail,
                displayCompany,
                cleanRole,
                response.getAtsScore(),
                nowFormatted,
                resultJson
        );
        resumeHistoryRepository.save(history);

        return response;
    }

    private String toTitleCase(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        String[] words = input.split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            if (!word.isEmpty()) {
                sb.append(Character.toUpperCase(word.charAt(0)));
                if (word.length() > 1) {
                    sb.append(word.substring(1).toLowerCase());
                }
            }
            if (i < words.length - 1) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }
}
