package com.resumeanalyzer.controller;

import com.resumeanalyzer.dto.AnalysisResponseDto;
import com.resumeanalyzer.dto.MessageResponse;
import com.resumeanalyzer.dto.ResumeHistoryItemDto;
import com.resumeanalyzer.service.HistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(name = "History", description = "Endpoints for managing user resume analysis history")
public class HistoryController {

    private final HistoryService historyService;

    public HistoryController(HistoryService historyService) {
        this.historyService = historyService;
    }

    @GetMapping("/history/{email}")
    @Operation(summary = "Get user analysis history", description = "Retrieves all past resume analysis summaries for a given user email.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of history items returned successfully")
    })
    public ResponseEntity<List<ResumeHistoryItemDto>> getHistory(
            @Parameter(description = "User account email address", required = true)
            @PathVariable("email") String email
    ) {
        List<ResumeHistoryItemDto> history = historyService.getHistory(email);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/history/result/{history_id}")
    @Operation(summary = "Get specific analysis result", description = "Retrieves the full analysis result JSON for a specific history record ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Full analysis result returned successfully"),
            @ApiResponse(responseCode = "404", description = "History record not found")
    })
    public ResponseEntity<AnalysisResponseDto> getHistoryResult(
            @Parameter(description = "Unique numeric ID of the history record", required = true)
            @PathVariable("history_id") Long historyId
    ) {
        AnalysisResponseDto result = historyService.getHistoryResult(historyId);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/history/{history_id}")
    @Operation(summary = "Delete an analysis history record", description = "Deletes a specific history record by its ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Record deleted successfully"),
            @ApiResponse(responseCode = "404", description = "History record not found")
    })
    public ResponseEntity<MessageResponse> deleteHistory(
            @Parameter(description = "Unique numeric ID of the history record to delete", required = true)
            @PathVariable("history_id") Long historyId
    ) {
        MessageResponse response = historyService.deleteHistory(historyId);
        return ResponseEntity.ok(response);
    }
}

