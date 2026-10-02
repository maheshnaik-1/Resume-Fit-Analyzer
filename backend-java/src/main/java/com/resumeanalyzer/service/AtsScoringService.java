package com.resumeanalyzer.service;

import com.resumeanalyzer.dto.AtsCalculationResultDto;
import com.resumeanalyzer.dto.ScoreBreakdownDto;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class AtsScoringService {

    public static final double WEIGHT_SKILLS = 0.70;
    public static final double WEIGHT_PROJECTS = 0.15;
    public static final double WEIGHT_EDUCATION = 0.10;
    public static final double WEIGHT_CERTIFICATIONS = 0.05;

    public int calculateSkillsScore(int matchedCount, int jobSkillsCount) {
        if (jobSkillsCount <= 0 || matchedCount <= 0) {
            return 0;
        }
        double percentage = ((double) matchedCount / (double) jobSkillsCount) * 100.0;
        return BigDecimal.valueOf(percentage).setScale(0, RoundingMode.HALF_EVEN).intValue();
    }

    public int calculateProjectsScore(int detectedProjectsCount) {
        if (detectedProjectsCount >= 3) {
            return 100;
        } else if (detectedProjectsCount == 2) {
            return 75;
        } else if (detectedProjectsCount == 1) {
            return 50;
        } else {
            return 0;
        }
    }

    public int calculateEducationScore(List<String> detectedEducation) {
        if (detectedEducation != null && !detectedEducation.isEmpty()) {
            return 100;
        }
        return 0;
    }

    public int calculateCertificationScore(List<String> detectedCertifications) {
        int count = detectedCertifications != null ? detectedCertifications.size() : 0;
        if (count >= 3) {
            return 100;
        } else if (count == 2) {
            return 75;
        } else if (count == 1) {
            return 50;
        } else {
            return 0;
        }
    }

    public double calculateFinalAtsScore(
            int skillsScore,
            int projectsScore,
            int educationScore,
            int certificationScore
    ) {
        double rawScore = (skillsScore * WEIGHT_SKILLS)
                + (projectsScore * WEIGHT_PROJECTS)
                + (educationScore * WEIGHT_EDUCATION)
                + (certificationScore * WEIGHT_CERTIFICATIONS);

        return BigDecimal.valueOf(rawScore).setScale(2, RoundingMode.HALF_EVEN).doubleValue();
    }

    public AtsCalculationResultDto calculateAtsScore(
            List<String> matchedSkills,
            List<String> jobSkills,
            List<String> detectedProjects,
            List<String> detectedEducation,
            List<String> detectedCertifications
    ) {
        int matchedCount = matchedSkills != null ? matchedSkills.size() : 0;
        int jobSkillsCount = jobSkills != null ? jobSkills.size() : 0;
        int projectsCount = detectedProjects != null ? detectedProjects.size() : 0;

        int skillsScore = calculateSkillsScore(matchedCount, jobSkillsCount);
        int projectsScore = calculateProjectsScore(projectsCount);
        int educationScore = calculateEducationScore(detectedEducation);
        int certificationScore = calculateCertificationScore(detectedCertifications);

        double atsScore = calculateFinalAtsScore(
                skillsScore,
                projectsScore,
                educationScore,
                certificationScore
        );

        ScoreBreakdownDto scoreBreakdown = new ScoreBreakdownDto(
                skillsScore,
                projectsScore,
                educationScore,
                certificationScore
        );

        return new AtsCalculationResultDto(atsScore, scoreBreakdown);
    }
}
