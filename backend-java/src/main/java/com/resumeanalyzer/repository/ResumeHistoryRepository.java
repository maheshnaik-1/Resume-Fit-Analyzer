package com.resumeanalyzer.repository;

import com.resumeanalyzer.entity.ResumeHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResumeHistoryRepository extends JpaRepository<ResumeHistory, Long> {

    /**
     * Retrieves resume history for a user ordered by analyzed_at descending.
     * Exact V1.0 Python equivalent:
     * SELECT id, company, role, ats_score, analyzed_at FROM resume_history WHERE email = ? ORDER BY analyzed_at DESC
     */
    List<ResumeHistory> findByEmailOrderByAnalyzedAtDesc(String email);

    /**
     * Retrieves all history records for a user (used for dashboard statistics calculation).
     * Exact V1.0 Python equivalent:
     * SELECT ats_score, company FROM resume_history WHERE email = ?
     */
    List<ResumeHistory> findByEmail(String email);

    /**
     * Retrieves the best resume record for a user by highest ATS score.
     * Exact V1.0 Python equivalent:
     * SELECT company, role, ats_score, analyzed_at FROM resume_history WHERE email = ? ORDER BY ats_score DESC LIMIT 1
     */
    Optional<ResumeHistory> findFirstByEmailOrderByAtsScoreDesc(String email);

    /**
     * Retrieves the last 3 resume history records for improvement calculation matching company and role case-insensitively.
     * Exact V1.0 Python equivalent:
     * SELECT ats_score, analyzed_at FROM resume_history WHERE email = ? AND LOWER(TRIM(company)) = LOWER(TRIM(?)) AND LOWER(TRIM(role)) = LOWER(TRIM(?)) ORDER BY analyzed_at DESC LIMIT 3
     */
    @Query("SELECT r FROM ResumeHistory r WHERE r.email = :email AND LOWER(TRIM(r.company)) = LOWER(TRIM(:company)) AND LOWER(TRIM(r.role)) = LOWER(TRIM(:role)) ORDER BY r.analyzedAt DESC LIMIT 3")
    List<ResumeHistory> findTop3ImprovementHistory(
            @Param("email") String email,
            @Param("company") String company,
            @Param("role") String role
    );
}
