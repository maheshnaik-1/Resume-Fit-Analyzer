package com.resumeanalyzer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public class ResumeHistoryItemDto {

    private Long id;
    private String company;
    private String role;

    @JsonProperty("ats_score")
    private Double atsScore;

    @JsonProperty("analyzed_at")
    private String analyzedAt;

    public ResumeHistoryItemDto() {
    }

    public ResumeHistoryItemDto(Long id, String company, String role, Double atsScore, String analyzedAt) {
        this.id = id;
        this.company = company;
        this.role = role;
        this.atsScore = atsScore;
        this.analyzedAt = analyzedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Double getAtsScore() {
        return atsScore;
    }

    public void setAtsScore(Double atsScore) {
        this.atsScore = atsScore;
    }

    public String getAnalyzedAt() {
        return analyzedAt;
    }

    public void setAnalyzedAt(String analyzedAt) {
        this.analyzedAt = analyzedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResumeHistoryItemDto that = (ResumeHistoryItemDto) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(company, that.company) &&
                Objects.equals(role, that.role) &&
                Objects.equals(atsScore, that.atsScore) &&
                Objects.equals(analyzedAt, that.analyzedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, company, role, atsScore, analyzedAt);
    }

    @Override
    public String toString() {
        return "ResumeHistoryItemDto{" +
                "id=" + id +
                ", company='" + company + '\'' +
                ", role='" + role + '\'' +
                ", atsScore=" + atsScore +
                ", analyzedAt='" + analyzedAt + '\'' +
                '}';
    }
}
