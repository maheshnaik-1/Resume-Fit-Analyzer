package com.resumeanalyzer.controller;

import com.resumeanalyzer.dto.AnalysisResponseDto;
import com.resumeanalyzer.dto.ErrorDetailResponse;
import com.resumeanalyzer.dto.MessageResponse;
import com.resumeanalyzer.dto.ResumeHistoryItemDto;
import com.resumeanalyzer.exception.ResourceNotFoundException;
import com.resumeanalyzer.service.HistoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class HistoryController {

    private final HistoryService historyService;

    public HistoryController(HistoryService historyService) {
        this.historyService = historyService;
    }

    @GetMapping("/history/{email}")
    public ResponseEntity<List<ResumeHistoryItemDto>> getHistory(@PathVariable("email") String email) {
        List<ResumeHistoryItemDto> history = historyService.getHistory(email);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/history/result/{history_id}")
    public ResponseEntity<AnalysisResponseDto> getHistoryResult(@PathVariable("history_id") Long historyId) {
        AnalysisResponseDto result = historyService.getHistoryResult(historyId);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/history/{history_id}")
    public ResponseEntity<MessageResponse> deleteHistory(@PathVariable("history_id") Long historyId) {
        MessageResponse response = historyService.deleteHistory(historyId);
        return ResponseEntity.ok(response);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDetailResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorDetailResponse(ex.getMessage()));
    }
}
