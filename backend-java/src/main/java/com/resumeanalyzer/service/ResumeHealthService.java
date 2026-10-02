package com.resumeanalyzer.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ResumeHealthService {

    public List<String> calculateResumeHealth(
            int skillsScore,
            int projectsScore,
            int educationScore,
            int certificationScore
    ) {
        List<String> resumeHealth = new ArrayList<>();

        // 1. Skills Health
        if (skillsScore >= 80) {
            resumeHealth.add("🟢 Strong technical skills");
        } else if (skillsScore >= 50) {
            resumeHealth.add("🟡 Skills section can be improved");
        } else {
            resumeHealth.add("🔴 Add more relevant technical skills");
        }

        // 2. Projects Health
        if (projectsScore >= 80) {
            resumeHealth.add("🟢 Excellent project portfolio");
        } else if (projectsScore >= 50) {
            resumeHealth.add("🟡 Add one stronger project");
        } else {
            resumeHealth.add("🔴 Projects section is weak");
        }

        // 3. Education Health
        if (educationScore >= 80) {
            resumeHealth.add("🟢 Education section looks complete");
        } else {
            resumeHealth.add("🟡 Improve education details");
        }

        // 4. Certifications Health
        if (certificationScore >= 80) {
            resumeHealth.add("🟢 Certifications strengthen your profile");
        } else if (certificationScore >= 50) {
            resumeHealth.add("🟡 Add more certifications");
        } else {
            resumeHealth.add("🔴 Certifications are missing");
        }

        return resumeHealth;
    }
}
