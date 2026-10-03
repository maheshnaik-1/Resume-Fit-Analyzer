package com.resumeanalyzer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.resumeanalyzer.dto.AnalysisResponseDto;
import com.resumeanalyzer.dto.MessageResponse;
import com.resumeanalyzer.dto.ResumeHistoryItemDto;
import com.resumeanalyzer.entity.ResumeHistory;
import com.resumeanalyzer.exception.ResourceNotFoundException;
import com.resumeanalyzer.repository.ResumeHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HistoryService {

    private final ResumeHistoryRepository resumeHistoryRepository;
    private final ObjectMapper objectMapper;

    public HistoryService(ResumeHistoryRepository resumeHistoryRepository, ObjectMapper objectMapper) {
        this.resumeHistoryRepository = resumeHistoryRepository;
        this.objectMapper = objectMapper;
    }

    public List<ResumeHistoryItemDto> getHistory(String email) {
        if (email == null) {
            return Collections.emptyList();
        }
        String normalizedEmail = email.trim().toLowerCase();
        List<ResumeHistory> records = resumeHistoryRepository.findByEmailOrderByAnalyzedAtDesc(normalizedEmail);
        return records.stream()
                .map(r -> new ResumeHistoryItemDto(
                        r.getId(),
                        r.getCompany(),
                        r.getRole(),
                        r.getAtsScore(),
                        r.getAnalyzedAt()
                ))
                .collect(Collectors.toList());
    }

    public AnalysisResponseDto getHistoryResult(Long historyId) {
        if (historyId == null) {
            throw new ResourceNotFoundException("History record not found");
        }
        Optional<ResumeHistory> historyOpt = resumeHistoryRepository.findById(historyId);
        if (historyOpt.isEmpty() || historyOpt.get().getResultJson() == null || historyOpt.get().getResultJson().isBlank()) {
            throw new ResourceNotFoundException("History record not found");
        }

        try {
            return objectMapper.readValue(historyOpt.get().getResultJson(), AnalysisResponseDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse history result JSON", e);
        }
    }

    @Transactional
    public MessageResponse deleteHistory(Long historyId) {
        if (historyId != null && resumeHistoryRepository.existsById(historyId)) {
            resumeHistoryRepository.deleteById(historyId);
        }
        return new MessageResponse("History deleted successfully");
    }
}
