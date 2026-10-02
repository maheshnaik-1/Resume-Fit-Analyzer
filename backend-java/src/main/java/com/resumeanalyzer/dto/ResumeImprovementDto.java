package com.resumeanalyzer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Objects;

public class ResumeImprovementDto {
    private List<ResumeImprovementHistoryDto> history;

    @JsonProperty("first_score")
    private double firstScore;

    @JsonProperty("latest_score")
    private double latestScore;

    private double improvement;

    public ResumeImprovementDto() {
    }

    public ResumeImprovementDto(List<ResumeImprovementHistoryDto> history, double firstScore, double latestScore, double improvement) {
        this.history = history;
        this.firstScore = firstScore;
        this.latestScore = latestScore;
        this.improvement = improvement;
    }

    public List<ResumeImprovementHistoryDto> getHistory() {
        return history;
    }

    public void setHistory(List<ResumeImprovementHistoryDto> history) {
        this.history = history;
    }

    public double getFirstScore() {
        return firstScore;
    }

    public void setFirstScore(double firstScore) {
        this.firstScore = firstScore;
    }

    public double getLatestScore() {
        return latestScore;
    }

    public void setLatestScore(double latestScore) {
        this.latestScore = latestScore;
    }

    public double getImprovement() {
        return improvement;
    }

    public void setImprovement(double improvement) {
        this.improvement = improvement;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResumeImprovementDto that = (ResumeImprovementDto) o;
        return Double.compare(that.firstScore, firstScore) == 0 &&
                Double.compare(that.latestScore, latestScore) == 0 &&
                Double.compare(that.improvement, improvement) == 0 &&
                Objects.equals(history, that.history);
    }

    @Override
    public int hashCode() {
        return Objects.hash(history, firstScore, latestScore, improvement);
    }

    @Override
    public String toString() {
        return "ResumeImprovementDto{" +
                "history=" + history +
                ", firstScore=" + firstScore +
                ", latestScore=" + latestScore +
                ", improvement=" + improvement +
                '}';
    }
}
