package com.resumeanalyzer.service;

import com.resumeanalyzer.catalog.CompanySuggestionCatalog;
import com.resumeanalyzer.dto.RoleMatchDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SuggestionService {

    public List<String> generateSuggestions(
            List<RoleMatchDto> topMatches,
            List<String> missingSkills,
            double atsScore,
            List<String> detectedProjects,
            List<String> detectedCertifications,
            String company,
            String role
    ) {
        List<String> suggestions = new ArrayList<>();

        // 1. Career Role Match Suggestion
        if (topMatches != null && !topMatches.isEmpty() && topMatches.get(0).getMatchScore() > 0) {
            String topRole = topMatches.get(0).getRole();
            double topScore = topMatches.get(0).getMatchScore();

            suggestions.add("Your resume has the highest skill match for the '" + topRole + "' role (" + Double.toString(topScore) + "% match).");
            if (topScore < 60) {
                suggestions.add("Consider adding more core technical skills to improve your role match score.");
            }
        } else {
            suggestions.add("Add more relevant technical skills to your resume to improve role match scores.");
        }

        // 2. Missing Skills Suggestions
        if (missingSkills != null) {
            for (String skill : missingSkills) {
                suggestions.add("Learn " + skill + " through practical projects.");
            }
        }

        // 3. ATS Score Bracket Suggestions
        if (atsScore < 60) {
            suggestions.add("Build 1-2 projects related to your target job role.");
            suggestions.add("Tailor your resume for each job application.");
        } else if (atsScore < 80) {
            suggestions.add("Strengthen your technical skills with certifications.");
            suggestions.add("Add measurable achievements to your projects.");
        }

        // 4. Resume Quality Suggestions
        int projectsCount = detectedProjects != null ? detectedProjects.size() : 0;
        if (projectsCount < 2) {
            suggestions.add("Include more personal or academic projects.");
        }

        int certsCount = detectedCertifications != null ? detectedCertifications.size() : 0;
        if (certsCount == 0) {
            suggestions.add("Earn at least one relevant certification.");
        }

        suggestions.add("Improve resume keywords for better ATS compatibility.");
        suggestions.add("Keep your GitHub and LinkedIn profiles updated.");

        // 5. Company-Specific Suggestions
        String companyName = company != null ? company.trim().toLowerCase() : "";
        String roleName = role != null ? role.trim().toLowerCase() : "";

        if (CompanySuggestionCatalog.COMPANY_SUGGESTIONS.containsKey(companyName)) {
            Map<String, List<String>> companyRoles = CompanySuggestionCatalog.COMPANY_SUGGESTIONS.get(companyName);
            if (companyRoles.containsKey(roleName)) {
                suggestions.addAll(companyRoles.get(roleName));
            } else if (companyRoles.containsKey("default")) {
                suggestions.addAll(companyRoles.get("default"));
            }
        }

        return suggestions;
    }
}
