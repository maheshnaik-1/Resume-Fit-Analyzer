package com.resumeanalyzer.service;

import com.resumeanalyzer.dto.BestResumeDto;
import com.resumeanalyzer.dto.ResumeImprovementDto;
import com.resumeanalyzer.dto.ResumeImprovementHistoryDto;
import com.resumeanalyzer.entity.ResumeHistory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ResumeImprovementService {

    public BestResumeDto getBestResumeRecord(List<ResumeHistory> userHistory) {
        if (userHistory == null || userHistory.isEmpty()) {
            return null;
        }

        ResumeHistory best = userHistory.get(0);
        for (ResumeHistory r : userHistory) {
            if (r.getAtsScore() > best.getAtsScore()) {
                best = r;
            }
        }

        return new BestResumeDto(
                best.getCompany(),
                best.getRole(),
                best.getAtsScore(),
                best.getAnalyzedAt()
        );
    }

    public ResumeImprovementDto calculateResumeImprovement(List<ResumeHistory> top3RecentHistory) {
        if (top3RecentHistory == null || top3RecentHistory.isEmpty()) {
            return null;
        }

        // top3RecentHistory is passed in descending analyzedAt order (LIMIT 3).
        // Reverse to chronological order (oldest to newest), matching Python V1.0 rows.reverse()
        List<ResumeHistory> chronological = new ArrayList<>(top3RecentHistory);
        Collections.reverse(chronological);

        List<ResumeImprovementHistoryDto> history = new ArrayList<>();
        for (ResumeHistory r : chronological) {
            history.add(new ResumeImprovementHistoryDto(r.getAtsScore(), r.getAnalyzedAt()));
        }

        double firstScore = history.get(0).getScore();
        double latestScore = history.get(history.size() - 1).getScore();
        double improvement = BigDecimal.valueOf(latestScore - firstScore)
                .setScale(2, RoundingMode.HALF_EVEN)
                .doubleValue();

        return new ResumeImprovementDto(history, firstScore, latestScore, improvement);
    }
}
