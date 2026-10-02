package com.resumeanalyzer.service;

import com.resumeanalyzer.catalog.RoleCatalog;
import com.resumeanalyzer.dto.RoleMatchDto;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RoleMatchingService {

    public static final int DEFAULT_TOP_N = 3;

    public List<RoleMatchDto> matchRoles(List<String> detectedSkills) {
        return matchRoles(detectedSkills, DEFAULT_TOP_N);
    }

    public List<RoleMatchDto> matchRoles(List<String> detectedSkills, int topN) {
        Set<String> detectedSet = (detectedSkills != null)
                ? new HashSet<>(detectedSkills)
                : Collections.emptySet();

        List<RoleMatchDto> scoredRoles = new ArrayList<>();

        for (Map.Entry<String, List<String>> entry : RoleCatalog.ROLE_SKILLS.entrySet()) {
            String role = entry.getKey();
            List<String> requiredSkills = entry.getValue();

            if (requiredSkills == null || requiredSkills.isEmpty()) {
                continue;
            }

            Set<String> reqSet = new HashSet<>(requiredSkills);
            long overlapCount = reqSet.stream().filter(detectedSet::contains).count();

            double rawPercentage = ((double) overlapCount / (double) reqSet.size()) * 100.0;
            double matchScore = BigDecimal.valueOf(rawPercentage)
                    .setScale(2, RoundingMode.HALF_EVEN)
                    .doubleValue();

            scoredRoles.add(new RoleMatchDto(role, matchScore));
        }

        // Sort descending by matchScore, then alphabetically by role name for stable ranking
        scoredRoles.sort((a, b) -> {
            int scoreCompare = Double.compare(b.getMatchScore(), a.getMatchScore());
            if (scoreCompare != 0) {
                return scoreCompare;
            }
            return a.getRole().compareTo(b.getRole());
        });

        int size = scoredRoles.size();
        int limit = (topN >= 0)
                ? Math.min(topN, size)
                : Math.max(0, size + topN);

        return new ArrayList<>(scoredRoles.subList(0, limit));
    }

    public List<String> extractRecommendedRoles(List<RoleMatchDto> topMatches) {
        if (topMatches == null || topMatches.isEmpty()) {
            return Collections.emptyList();
        }
        return topMatches.stream()
                .map(RoleMatchDto::getRole)
                .collect(Collectors.toList());
    }
}
