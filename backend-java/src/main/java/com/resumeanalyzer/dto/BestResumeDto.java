package com.resumeanalyzer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public class BestResumeDto {
    private String company;
    private String role;

    @JsonProperty("ats_score")
    private double atsScore;

    private String date;

    public BestResumeDto() {
    }

    public BestResumeDto(String company, String role, double atsScore, String date) {
        this.company = company;
        this.role = role;
        this.atsScore = atsScore;
        this.date = date;
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

    public double getAtsScore() {
        return atsScore;
    }

    public void setAtsScore(double atsScore) {
        this.atsScore = atsScore;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BestResumeDto that = (BestResumeDto) o;
        return Double.compare(that.atsScore, atsScore) == 0 &&
                Objects.equals(company, that.company) &&
                Objects.equals(role, that.role) &&
                Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(company, role, atsScore, date);
    }

    @Override
    public String toString() {
        return "BestResumeDto{" +
                "company='" + company + '\'' +
                ", role='" + role + '\'' +
                ", atsScore=" + atsScore +
                ", date='" + date + '\'' +
                '}';
    }
}
