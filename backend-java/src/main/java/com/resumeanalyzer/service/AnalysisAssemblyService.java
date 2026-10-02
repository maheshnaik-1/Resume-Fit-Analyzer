package com.resumeanalyzer.service;

import com.resumeanalyzer.dto.AnalysisResponseDto;
import com.resumeanalyzer.dto.AtsCalculationResultDto;
import com.resumeanalyzer.dto.BestResumeDto;
import com.resumeanalyzer.dto.ResumeImprovementDto;
import com.resumeanalyzer.dto.ResumeSummaryDto;
import com.resumeanalyzer.dto.RoleMatchDto;
import com.resumeanalyzer.dto.ScoreBreakdownDto;
import com.resumeanalyzer.dto.SkillAnalysisResultDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnalysisAssemblyService {

    private final ResumeSectionParserService sectionParserService;
    private final SkillEngineService skillEngineService;
    private final AtsScoringService atsScoringService;
    private final RoleMatchingService roleMatchingService;
    private final SuggestionService suggestionService;
    private final ResumeHealthService resumeHealthService;
    private final ResumeImprovementService resumeImprovementService;

    @Autowired
    public AnalysisAssemblyService(
            ResumeSectionParserService sectionParserService,
            SkillEngineService skillEngineService,
            AtsScoringService atsScoringService,
            RoleMatchingService roleMatchingService,
            SuggestionService suggestionService,
            ResumeHealthService resumeHealthService,
            ResumeImprovementService resumeImprovementService
    ) {
        this.sectionParserService = sectionParserService;
        this.skillEngineService = skillEngineService;
        this.atsScoringService = atsScoringService;
        this.roleMatchingService = roleMatchingService;
        this.suggestionService = suggestionService;
        this.resumeHealthService = resumeHealthService;
        this.resumeImprovementService = resumeImprovementService;
    }

    public AnalysisResponseDto assembleAnalysis(
            String filename,
            String resumeText,
            String jobDescription,
            String company,
            String role,
            double analysisTime,
            BestResumeDto bestResume,
            ResumeImprovementDto resumeImprovement
    ) {
        String cleanCompany = company != null ? company.trim() : "";
        String cleanRole = role != null ? role.trim() : "";
        String displayCompany = toTitleCase(cleanCompany);
        String text = resumeText != null ? resumeText : "";
        String jd = jobDescription != null ? jobDescription : "";

        // 1. Parse Resume Sections
        List<String> detectedEducation = sectionParserService.extractEducation(text);
        List<String> detectedCertifications = sectionParserService.extractCertifications(text);
        List<String> detectedProjects = sectionParserService.extractProjects(text);

        // 2. Skill Detection & Gap Matching
        SkillAnalysisResultDto skillResult = skillEngineService.analyze(text, jd);
        List<String> detectedSkills = skillResult.getDetectedSkills();
        List<String> jobSkills = skillResult.getJobSkills();
        List<String> matchedSkills = skillResult.getMatchedSkills();
        List<String> missingSkills = skillResult.getMissingSkills();

        // 3. ATS Scoring & Score Breakdown
        AtsCalculationResultDto atsResult = atsScoringService.calculateAtsScore(
                matchedSkills,
                jobSkills,
                detectedProjects,
                detectedEducation,
                detectedCertifications
        );
        double atsScore = atsResult.getAtsScore();
        ScoreBreakdownDto scoreBreakdown = atsResult.getScoreBreakdown();

        // 4. Role Matching
        List<RoleMatchDto> topMatches = roleMatchingService.matchRoles(detectedSkills, 3);
        List<String> recommendedRoles = roleMatchingService.extractRecommendedRoles(topMatches);

        // 5. Suggestions
        List<String> suggestions = suggestionService.generateSuggestions(
                topMatches,
                missingSkills,
                atsScore,
                detectedProjects,
                detectedCertifications,
                cleanCompany,
                cleanRole
        );

        // 6. Resume Health Report
        List<String> resumeHealth = resumeHealthService.calculateResumeHealth(
                scoreBreakdown.getSkills(),
                scoreBreakdown.getProjects(),
                scoreBreakdown.getEducation(),
                scoreBreakdown.getCertifications()
        );

        // 7. Resume Summary Object
        ResumeSummaryDto resumeSummary = new ResumeSummaryDto(
                detectedEducation,
                detectedProjects,
                detectedCertifications,
                detectedSkills
        );

        // 8. Construct Final Response DTO
        return new AnalysisResponseDto(
                filename != null ? filename : "",
                displayCompany,
                cleanRole,
                displayCompany,
                cleanRole,
                resumeSummary,
                jobSkills,
                matchedSkills,
                missingSkills,
                atsScore,
                scoreBreakdown,
                recommendedRoles,
                topMatches,
                analysisTime,
                resumeHealth,
                suggestions,
                bestResume,
                resumeImprovement
        );
    }

    public AnalysisResponseDto assembleAnalysis(
            String filename,
            String resumeText,
            String jobDescription,
            String company,
            String role,
            double analysisTime
    ) {
        return assembleAnalysis(filename, resumeText, jobDescription, company, role, analysisTime, null, null);
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
