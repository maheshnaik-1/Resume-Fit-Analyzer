package com.resumeanalyzer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public class DashboardStatsDto {

    @JsonProperty("total_analyses")
    private Integer totalAnalyses;

    @JsonProperty("highest_score")
    private Double highestScore;

    @JsonProperty("average_score")
    private Double averageScore;

    @JsonProperty("companies")
    private Integer companies;

    public DashboardStatsDto() {
    }

    public DashboardStatsDto(Integer totalAnalyses, Double highestScore, Double averageScore, Integer companies) {
        this.totalAnalyses = totalAnalyses;
        this.highestScore = highestScore;
        this.averageScore = averageScore;
        this.companies = companies;
    }

    public Integer getTotalAnalyses() {
        return totalAnalyses;
    }

    public void setTotalAnalyses(Integer totalAnalyses) {
        this.totalAnalyses = totalAnalyses;
    }

    public Double getHighestScore() {
        return highestScore;
    }

    public void setHighestScore(Double highestScore) {
        this.highestScore = highestScore;
    }

    public Double getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(Double averageScore) {
        this.averageScore = averageScore;
    }

    public Integer getCompanies() {
        return companies;
    }

    public void setCompanies(Integer companies) {
        this.companies = companies;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DashboardStatsDto that = (DashboardStatsDto) o;
        return Objects.equals(totalAnalyses, that.totalAnalyses) &&
                Objects.equals(highestScore, that.highestScore) &&
                Objects.equals(averageScore, that.averageScore) &&
                Objects.equals(companies, that.companies);
    }

    @Override
    public int hashCode() {
        return Objects.hash(totalAnalyses, highestScore, averageScore, companies);
    }

    @Override
    public String toString() {
        return "DashboardStatsDto{" +
                "totalAnalyses=" + totalAnalyses +
                ", highestScore=" + highestScore +
                ", averageScore=" + averageScore +
                ", companies=" + companies +
                '}';
    }
}
