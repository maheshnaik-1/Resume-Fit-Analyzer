package com.resumeanalyzer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "resume_history")
public class ResumeHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email")
    private String email;

    @Column(name = "company")
    private String company;

    @Column(name = "role")
    private String role;

    @Column(name = "ats_score")
    private Double atsScore;

    @Column(name = "analyzed_at")
    private String analyzedAt;

    @Column(name = "result_json", columnDefinition = "TEXT")
    private String resultJson;

    public ResumeHistory() {
    }

    public ResumeHistory(String email, String company, String role, Double atsScore, String analyzedAt, String resultJson) {
        this.email = email;
        this.company = company;
        this.role = role;
        this.atsScore = atsScore;
        this.analyzedAt = analyzedAt;
        this.resultJson = resultJson;
    }

    public ResumeHistory(Long id, String email, String company, String role, Double atsScore, String analyzedAt, String resultJson) {
        this.id = id;
        this.email = email;
        this.company = company;
        this.role = role;
        this.atsScore = atsScore;
        this.analyzedAt = analyzedAt;
        this.resultJson = resultJson;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getResultJson() {
        return resultJson;
    }

    public void setResultJson(String resultJson) {
        this.resultJson = resultJson;
    }
}
