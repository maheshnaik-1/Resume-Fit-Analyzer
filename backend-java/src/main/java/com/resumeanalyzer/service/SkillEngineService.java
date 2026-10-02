package com.resumeanalyzer.service;

import com.resumeanalyzer.catalog.SkillCatalog;
import com.resumeanalyzer.dto.SkillAnalysisResultDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

@Service
public class SkillEngineService {

    // Precompiled regex patterns for resume skill detection (alias-aware)
    private static final Map<String, List<Pattern>> RESUME_PATTERNS = new LinkedHashMap<>();

    // Precompiled regex patterns for job description skill detection (canonical-only)
    private static final Map<String, Pattern> JOB_PATTERNS = new LinkedHashMap<>();

    private static final Pattern C_PATTERN = Pattern.compile("(?<!\\w)C(?!\\w|[+#])", Pattern.CASE_INSENSITIVE);

    static {
        for (String skill : SkillCatalog.CANONICAL_SKILLS) {
            // Build Resume Patterns (using aliases)
            List<String> aliases = SkillCatalog.getAliasesForSkill(skill);
            List<Pattern> aliasPatterns = new ArrayList<>();
            for (String alias : aliases) {
                if ("C".equals(alias)) {
                    aliasPatterns.add(C_PATTERN);
                } else {
                    aliasPatterns.add(Pattern.compile("(?<!\\w)" + Pattern.quote(alias) + "(?!\\w)", Pattern.CASE_INSENSITIVE));
                }
            }
            RESUME_PATTERNS.put(skill, Collections.unmodifiableList(aliasPatterns));

            // Build Job Description Pattern (canonical name only)
            if ("C".equals(skill)) {
                JOB_PATTERNS.put(skill, C_PATTERN);
            } else {
                JOB_PATTERNS.put(skill, Pattern.compile("(?<!\\w)" + Pattern.quote(skill) + "(?!\\w)", Pattern.CASE_INSENSITIVE));
            }
        }
    }

    public List<String> detectResumeSkills(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        Set<String> detected = new HashSet<>();

        for (String skill : SkillCatalog.CANONICAL_SKILLS) {
            List<Pattern> patterns = RESUME_PATTERNS.get(skill);
            if (patterns != null) {
                for (Pattern pattern : patterns) {
                    if (pattern.matcher(text).find()) {
                        detected.add(skill);
                        break;
                    }
                }
            }
        }

        return detected.stream().sorted().toList();
    }

    public List<String> detectJobSkills(String jobDescription) {
        if (jobDescription == null || jobDescription.isBlank()) {
            return Collections.emptyList();
        }

        List<String> jobSkills = new ArrayList<>();

        for (String skill : SkillCatalog.CANONICAL_SKILLS) {
            Pattern pattern = JOB_PATTERNS.get(skill);
            if (pattern != null && pattern.matcher(jobDescription).find()) {
                jobSkills.add(skill);
            }
        }

        return jobSkills;
    }

    public List<String> calculateMatchedSkills(List<String> jobSkills, List<String> detectedSkills) {
        if (jobSkills == null || jobSkills.isEmpty() || detectedSkills == null || detectedSkills.isEmpty()) {
            return Collections.emptyList();
        }

        Set<String> detectedSet = new HashSet<>(detectedSkills);
        return jobSkills.stream()
                .filter(detectedSet::contains)
                .toList();
    }

    public List<String> calculateMissingSkills(List<String> jobSkills, List<String> detectedSkills) {
        if (jobSkills == null || jobSkills.isEmpty()) {
            return Collections.emptyList();
        }

        Set<String> detectedSet = detectedSkills != null ? new HashSet<>(detectedSkills) : Collections.emptySet();
        return jobSkills.stream()
                .filter(skill -> !detectedSet.contains(skill))
                .toList();
    }

    public SkillAnalysisResultDto analyze(String resumeText, String jobDescription) {
        List<String> detectedSkills = detectResumeSkills(resumeText);
        List<String> jobSkills = detectJobSkills(jobDescription);
        List<String> matchedSkills = calculateMatchedSkills(jobSkills, detectedSkills);
        List<String> missingSkills = calculateMissingSkills(jobSkills, detectedSkills);

        return new SkillAnalysisResultDto(
                detectedSkills,
                jobSkills,
                matchedSkills,
                missingSkills
        );
    }
}
