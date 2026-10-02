package com.resumeanalyzer.dto;

import java.util.Objects;

public class ScoreBreakdownDto {
    private int skills;
    private int projects;
    private int education;
    private int certifications;

    public ScoreBreakdownDto() {
    }

    public ScoreBreakdownDto(int skills, int projects, int education, int certifications) {
        this.skills = skills;
        this.projects = projects;
        this.education = education;
        this.certifications = certifications;
    }

    public int getSkills() {
        return skills;
    }

    public void setSkills(int skills) {
        this.skills = skills;
    }

    public int getProjects() {
        return projects;
    }

    public void setProjects(int projects) {
        this.projects = projects;
    }

    public int getEducation() {
        return education;
    }

    public void setEducation(int education) {
        this.education = education;
    }

    public int getCertifications() {
        return certifications;
    }

    public void setCertifications(int certifications) {
        this.certifications = certifications;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScoreBreakdownDto that = (ScoreBreakdownDto) o;
        return skills == that.skills &&
                projects == that.projects &&
                education == that.education &&
                certifications == that.certifications;
    }

    @Override
    public int hashCode() {
        return Objects.hash(skills, projects, education, certifications);
    }

    @Override
    public String toString() {
        return "ScoreBreakdownDto{" +
                "skills=" + skills +
                ", projects=" + projects +
                ", education=" + education +
                ", certifications=" + certifications +
                '}';
    }
}
