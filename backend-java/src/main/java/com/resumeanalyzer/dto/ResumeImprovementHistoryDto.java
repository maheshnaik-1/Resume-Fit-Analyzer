package com.resumeanalyzer.dto;

import java.util.Objects;

public class ResumeImprovementHistoryDto {
    private double score;
    private String date;

    public ResumeImprovementHistoryDto() {
    }

    public ResumeImprovementHistoryDto(double score, String date) {
        this.score = score;
        this.date = date;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
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
        ResumeImprovementHistoryDto that = (ResumeImprovementHistoryDto) o;
        return Double.compare(that.score, score) == 0 &&
                Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(score, date);
    }

    @Override
    public String toString() {
        return "ResumeImprovementHistoryDto{" +
                "score=" + score +
                ", date='" + date + '\'' +
                '}';
    }
}
