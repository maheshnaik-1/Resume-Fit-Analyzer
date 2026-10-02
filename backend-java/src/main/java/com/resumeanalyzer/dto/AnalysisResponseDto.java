package com.resumeanalyzer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Objects;

public class AnalysisResponseDto {
    private String filename;
    private String company;
    private String role;

    @JsonProperty("target_company")
    private String targetCompany;

    @JsonProperty("target_role")
    private String targetRole;

    @JsonProperty("resume_summary")
    private ResumeSummaryDto resumeSummary;

    @JsonProperty("job_skills")
    private List<String> jobSkills;

    @JsonProperty("matched_skills")
    private List<String> matchedSkills;

    @JsonProperty("missing_skills")
    private List<String> missingSkills;

    @JsonProperty("ats_score")
    private double atsScore;

    @JsonProperty("score_breakdown")
    private ScoreBreakdownDto scoreBreakdown;

    @JsonProperty("recommended_roles")
    private List<String> recommendedRoles;

    @JsonProperty("top_matches")
    private List<RoleMatchDto> topMatches;

    @JsonProperty("analysis_time")
    private double analysisTime;

    @JsonProperty("resume_health")
    private List<String> resumeHealth;

    private List<String> suggestions;

    @JsonProperty("best_resume")
    private BestResumeDto bestResume;

    @JsonProperty("resume_improvement")
    private ResumeImprovementDto resumeImprovement;

    public AnalysisResponseDto() {
    }

    public AnalysisResponseDto(
            String filename,
            String company,
            String role,
            String targetCompany,
            String targetRole,
            ResumeSummaryDto resumeSummary,
            List<String> jobSkills,
            List<String> matchedSkills,
            List<String> missingSkills,
            double atsScore,
            ScoreBreakdownDto scoreBreakdown,
            List<String> recommendedRoles,
            List<RoleMatchDto> topMatches,
            double analysisTime,
            List<String> resumeHealth,
            List<String> suggestions,
            BestResumeDto bestResume,
            ResumeImprovementDto resumeImprovement
    ) {
        this.filename = filename;
        this.company = company;
        this.role = role;
        this.targetCompany = targetCompany;
        this.targetRole = targetRole;
        this.resumeSummary = resumeSummary;
        this.jobSkills = jobSkills;
        this.matchedSkills = matchedSkills;
        this.missingSkills = missingSkills;
        this.atsScore = atsScore;
        this.scoreBreakdown = scoreBreakdown;
        this.recommendedRoles = recommendedRoles;
        this.topMatches = topMatches;
        this.analysisTime = analysisTime;
        this.resumeHealth = resumeHealth;
        this.suggestions = suggestions;
        this.bestResume = bestResume;
        this.resumeImprovement = resumeImprovement;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
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

    public String getTargetCompany() {
        return targetCompany;
    }

    public void setTargetCompany(String targetCompany) {
        this.targetCompany = targetCompany;
    }

    public String getTargetRole() {
        return targetRole;
    }

    public void setTargetRole(String targetRole) {
        this.targetRole = targetRole;
    }

    public ResumeSummaryDto getResumeSummary() {
        return resumeSummary;
    }

    public void setResumeSummary(ResumeSummaryDto resumeSummary) {
        this.resumeSummary = resumeSummary;
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

    public List<String> getRecommendedRoles() {
        return recommendedRoles;
    }

    public void setRecommendedRoles(List<String> recommendedRoles) {
        this.recommendedRoles = recommendedRoles;
    }

    public List<RoleMatchDto> getTopMatches() {
        return topMatches;
    }

    public void setTopMatches(List<RoleMatchDto> topMatches) {
        this.topMatches = topMatches;
    }

    public double getAnalysisTime() {
        return analysisTime;
    }

    public void setAnalysisTime(double analysisTime) {
        this.analysisTime = analysisTime;
    }

    public List<String> getResumeHealth() {
        return resumeHealth;
    }

    public void setResumeHealth(List<String> resumeHealth) {
        this.resumeHealth = resumeHealth;
    }

    public List<String> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<String> suggestions) {
        this.suggestions = suggestions;
    }

    public BestResumeDto getBestResume() {
        return bestResume;
    }

    public void setBestResume(BestResumeDto bestResume) {
        this.bestResume = bestResume;
    }

    public ResumeImprovementDto getResumeImprovement() {
        return resumeImprovement;
    }

    public void setResumeImprovement(ResumeImprovementDto resumeImprovement) {
        this.resumeImprovement = resumeImprovement;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnalysisResponseDto that = (AnalysisResponseDto) o;
        return Double.compare(that.atsScore, atsScore) == 0 &&
                Double.compare(that.analysisTime, analysisTime) == 0 &&
                Objects.equals(filename, that.filename) &&
                Objects.equals(company, that.company) &&
                Objects.equals(role, that.role) &&
                Objects.equals(targetCompany, that.targetCompany) &&
                Objects.equals(targetRole, that.targetRole) &&
                Objects.equals(resumeSummary, that.resumeSummary) &&
                Objects.equals(jobSkills, that.jobSkills) &&
                Objects.equals(matchedSkills, that.matchedSkills) &&
                Objects.equals(missingSkills, that.missingSkills) &&
                Objects.equals(scoreBreakdown, that.scoreBreakdown) &&
                Objects.equals(recommendedRoles, that.recommendedRoles) &&
                Objects.equals(topMatches, that.topMatches) &&
                Objects.equals(resumeHealth, that.resumeHealth) &&
                Objects.equals(suggestions, that.suggestions) &&
                Objects.equals(bestResume, that.bestResume) &&
                Objects.equals(resumeImprovement, that.resumeImprovement);
    }

    @Override
    public int hashCode() {
        return Objects.hash(filename, company, role, targetCompany, targetRole, resumeSummary,
                jobSkills, matchedSkills, missingSkills, atsScore, scoreBreakdown,
                recommendedRoles, topMatches, analysisTime, resumeHealth, suggestions,
                bestResume, resumeImprovement);
    }
}
