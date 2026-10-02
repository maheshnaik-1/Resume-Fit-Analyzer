package com.resumeanalyzer.service;

import com.resumeanalyzer.dto.ResumeSummaryDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ResumeSectionParserService {

    // Precompiled Degree Patterns
    private static final Pattern B_TECH_PATTERN = Pattern.compile("\\b(B\\.?Tech|Bachelor of Technology)\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern M_TECH_PATTERN = Pattern.compile("\\bM\\.?Tech\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern BCA_PATTERN = Pattern.compile("\\bBCA\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern MCA_PATTERN = Pattern.compile("\\bMCA\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern B_SC_PATTERN = Pattern.compile("\\bB\\.?Sc\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern DIPLOMA_PATTERN = Pattern.compile("\\bDiploma\\b", Pattern.CASE_INSENSITIVE);

    // Precompiled Branch Patterns
    private static final Pattern CSE_PATTERN = Pattern.compile("\\b(Computer Science Engineering|CSE)\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern CS_PATTERN = Pattern.compile("\\bComputer Science\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern IT_PATTERN = Pattern.compile("\\b(Information Technology|IT)\\b", Pattern.CASE_INSENSITIVE);

    // Canonical Certification Database
    public static final List<String> CERTIFICATION_DATABASE = List.of(
            "Java Programming",
            "Python Programming",
            "Generative AI",
            "AWS",
            "Azure",
            "Google Cloud",
            "Machine Learning",
            "Data Science"
    );

    // Project Keywords
    public static final List<String> PROJECT_KEYWORDS = List.of(
            "project",
            "projects",
            "academic project",
            "personal project",
            "mini project",
            "major project"
    );

    public ResumeSummaryDto parseResumeSections(String text) {
        List<String> education = extractEducation(text);
        List<String> certifications = extractCertifications(text);
        List<String> projects = extractProjects(text);
        return new ResumeSummaryDto(education, projects, certifications);
    }

    public List<String> extractEducation(String text) {
        List<String> detectedEducation = new ArrayList<>();
        if (text == null || text.isBlank()) {
            return detectedEducation;
        }

        // Degree Detection
        if (B_TECH_PATTERN.matcher(text).find()) {
            detectedEducation.add("B.Tech");
        } else if (M_TECH_PATTERN.matcher(text).find()) {
            detectedEducation.add("M.Tech");
        } else if (BCA_PATTERN.matcher(text).find()) {
            detectedEducation.add("BCA");
        } else if (MCA_PATTERN.matcher(text).find()) {
            detectedEducation.add("MCA");
        } else if (B_SC_PATTERN.matcher(text).find()) {
            detectedEducation.add("B.Sc");
        } else if (DIPLOMA_PATTERN.matcher(text).find()) {
            detectedEducation.add("Diploma");
        }

        // Branch Detection
        if (CSE_PATTERN.matcher(text).find()) {
            detectedEducation.add("Computer Science Engineering");
        } else if (CS_PATTERN.matcher(text).find()) {
            detectedEducation.add("Computer Science");
        } else if (IT_PATTERN.matcher(text).find()) {
            detectedEducation.add("Information Technology");
        }

        return detectedEducation;
    }

    public List<String> extractCertifications(String text) {
        List<String> detectedCertifications = new ArrayList<>();
        if (text == null || text.isBlank()) {
            return detectedCertifications;
        }

        String lowerText = text.toLowerCase();
        for (String cert : CERTIFICATION_DATABASE) {
            if (lowerText.contains(cert.toLowerCase())) {
                detectedCertifications.add(cert);
            }
        }
        return detectedCertifications;
    }

    public List<String> extractProjects(String text) {
        List<String> detectedProjects = new ArrayList<>();
        if (text == null || text.isBlank()) {
            return detectedProjects;
        }

        int projectCount = 0;
        for (String keyword : PROJECT_KEYWORDS) {
            Pattern pattern = Pattern.compile(Pattern.quote(keyword), Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(text);
            while (matcher.find()) {
                projectCount++;
            }
        }

        int clamped = Math.min(projectCount, 3);
        if (clamped > 0) {
            detectedProjects.add(clamped + " Project" + (clamped != 1 ? "s" : "") + " Detected");
        }

        return detectedProjects;
    }
}
