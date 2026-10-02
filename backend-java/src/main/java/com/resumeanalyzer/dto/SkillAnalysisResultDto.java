package com.resumeanalyzer.dto;

import java.util.ArrayList;
import java.util.List;

public class SkillAnalysisResultDto {
    private List<String> detectedSkills = new ArrayList<>();
    private List<String> jobSkills = new ArrayList<>();
    private List<String> matchedSkills = new ArrayList<>();
    private List<String> missingSkills = new ArrayList<>();

    public SkillAnalysisResultDto() {
    }

    public SkillAnalysisResultDto(
            List<String> detectedSkills,
            List<String> jobSkills,
            List<String> matchedSkills,
            List<String> missingSkills
    ) {
        this.detectedSkills = detectedSkills != null ? detectedSkills : new ArrayList<>();
        this.jobSkills = jobSkills != null ? jobSkills : new ArrayList<>();
        this.matchedSkills = matchedSkills != null ? matchedSkills : new ArrayList<>();
        this.missingSkills = missingSkills != null ? missingSkills : new ArrayList<>();
    }

    public List<String> getDetectedSkills() {
        return detectedSkills;
    }

    public void setDetectedSkills(List<String> detectedSkills) {
        this.detectedSkills = detectedSkills;
    }

    public List<String> getJobSkills() {
        return jobSkills;
    }

    public void setJobSkills(List<String> jobSkills) {
        this.jobSkills = jobSkills;
    }

    public List<String> getMatchedSkills() {
        return matchedSkills;
    }

    public void setMatchedSkills(List<String> matchedSkills) {
        this.matchedSkills = matchedSkills;
    }

    public List<String> getMissingSkills() {
        return missingSkills;
    }

    public void setMissingSkills(List<String> missingSkills) {
        this.missingSkills = missingSkills;
    }
}
