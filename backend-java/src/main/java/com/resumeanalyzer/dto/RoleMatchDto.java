package com.resumeanalyzer.dto;

import java.util.Objects;

public class RoleMatchDto {
    private String role;
    private double matchScore;

    public RoleMatchDto() {
    }

    public RoleMatchDto(String role, double matchScore) {
        this.role = role;
        this.matchScore = matchScore;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public double getMatchScore() {
        return matchScore;
    }

    public void setMatchScore(double matchScore) {
        this.matchScore = matchScore;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoleMatchDto that = (RoleMatchDto) o;
        return Double.compare(that.matchScore, matchScore) == 0 &&
                Objects.equals(role, that.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(role, matchScore);
    }

    @Override
    public String toString() {
        return "RoleMatchDto{" +
                "role='" + role + '\'' +
                ", matchScore=" + matchScore +
                '}';
    }
}
