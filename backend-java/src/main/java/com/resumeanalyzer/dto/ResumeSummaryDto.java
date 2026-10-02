package com.resumeanalyzer.dto;

import java.util.ArrayList;
import java.util.List;

public class ResumeSummaryDto {
    private List<String> education = new ArrayList<>();
    private List<String> projects = new ArrayList<>();
    private List<String> certifications = new ArrayList<>();
    private List<String> skills = new ArrayList<>();

    public ResumeSummaryDto() {
    }

    public ResumeSummaryDto(List<String> education, List<String> projects, List<String> certifications) {
        this.education = education != null ? education : new ArrayList<>();
        this.projects = projects != null ? projects : new ArrayList<>();
        this.certifications = certifications != null ? certifications : new ArrayList<>();
        this.skills = new ArrayList<>();
    }

    public ResumeSummaryDto(List<String> education, List<String> projects, List<String> certifications, List<String> skills) {
        this.education = education != null ? education : new ArrayList<>();
        this.projects = projects != null ? projects : new ArrayList<>();
        this.certifications = certifications != null ? certifications : new ArrayList<>();
        this.skills = skills != null ? skills : new ArrayList<>();
    }

    public List<String> getEducation() {
        return education;
    }

    public void setEducation(List<String> education) {
        this.education = education;
    }

    public List<String> getProjects() {
        return projects;
    }

    public void setProjects(List<String> projects) {
        this.projects = projects;
    }

    public List<String> getCertifications() {
        return certifications;
    }

    public void setCertifications(List<String> certifications) {
        this.certifications = certifications;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResumeSummaryDto that = (ResumeSummaryDto) o;
        return java.util.Objects.equals(education, that.education) &&
                java.util.Objects.equals(projects, that.projects) &&
                java.util.Objects.equals(certifications, that.certifications) &&
                java.util.Objects.equals(skills, that.skills);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(education, projects, certifications, skills);
    }

    @Override
    public String toString() {
        return "ResumeSummaryDto{" +
                "education=" + education +
                ", projects=" + projects +
                ", certifications=" + certifications +
                ", skills=" + skills +
                '}';
    }
}
