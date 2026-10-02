package com.resumeanalyzer.dto;

import java.util.Objects;

public class AtsCalculationResultDto {
    private double atsScore;
    private ScoreBreakdownDto scoreBreakdown;

    public AtsCalculationResultDto() {
    }

    public AtsCalculationResultDto(double atsScore, ScoreBreakdownDto scoreBreakdown) {
        this.atsScore = atsScore;
        this.scoreBreakdown = scoreBreakdown;
    }

    public double getAtsScore() {
        return atsScore;
    }

    public void setAtsScore(double atsScore) {
        this.atsScore = atsScore;
    }

    public ScoreBreakdownDto getScoreBreakdown() {
        return scoreBreakdown;
    }

    public void setScoreBreakdown(ScoreBreakdownDto scoreBreakdown) {
        this.scoreBreakdown = scoreBreakdown;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AtsCalculationResultDto that = (AtsCalculationResultDto) o;
        return Double.compare(that.atsScore, atsScore) == 0 &&
                Objects.equals(scoreBreakdown, that.scoreBreakdown);
    }

    @Override
    public int hashCode() {
        return Objects.hash(atsScore, scoreBreakdown);
    }

    @Override
    public String toString() {
        return "AtsCalculationResultDto{" +
                "atsScore=" + atsScore +
                ", scoreBreakdown=" + scoreBreakdown +
                '}';
    }
}
