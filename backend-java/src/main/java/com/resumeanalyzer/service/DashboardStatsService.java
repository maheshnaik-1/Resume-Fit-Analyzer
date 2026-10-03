package com.resumeanalyzer.service;

import com.resumeanalyzer.dto.DashboardStatsDto;
import com.resumeanalyzer.entity.ResumeHistory;
import com.resumeanalyzer.repository.ResumeHistoryRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DashboardStatsService {

    private final ResumeHistoryRepository resumeHistoryRepository;

    public DashboardStatsService(ResumeHistoryRepository resumeHistoryRepository) {
        this.resumeHistoryRepository = resumeHistoryRepository;
    }

    public DashboardStatsDto getDashboardStats(String email) {
        if (email == null) {
            return new DashboardStatsDto(0, 0.0, 0.0, 0);
        }

        String normalizedEmail = email.trim().toLowerCase();
        List<ResumeHistory> records = resumeHistoryRepository.findByEmail(normalizedEmail);

        if (records == null || records.isEmpty()) {
            return new DashboardStatsDto(0, 0.0, 0.0, 0);
        }

        int totalAnalyses = records.size();

        double maxScore = records.stream()
                .mapToDouble(r -> r.getAtsScore() != null ? r.getAtsScore() : 0.0)
                .max()
                .orElse(0.0);
        double highestScore = BigDecimal.valueOf(maxScore)
                .setScale(2, RoundingMode.HALF_EVEN)
                .doubleValue();

        double sumScores = records.stream()
                .mapToDouble(r -> r.getAtsScore() != null ? r.getAtsScore() : 0.0)
                .sum();
        double avgScore = sumScores / totalAnalyses;
        double averageScore = BigDecimal.valueOf(avgScore)
                .setScale(2, RoundingMode.HALF_EVEN)
                .doubleValue();

        Set<String> distinctCompanies = records.stream()
                .map(ResumeHistory::getCompany)
                .filter(c -> c != null && !c.trim().isEmpty())
                .map(c -> c.trim().toLowerCase())
                .collect(Collectors.toSet());
        int companies = distinctCompanies.size();

        return new DashboardStatsDto(totalAnalyses, highestScore, averageScore, companies);
    }
}
