package com.resumeanalyzer.controller;

import com.resumeanalyzer.dto.MessageResponse;
import com.resumeanalyzer.dto.NotificationDto;
import com.resumeanalyzer.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(name = "Notifications", description = "Endpoints for user notifications and alerts")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/notifications/{email}")
    @Operation(summary = "Get user notifications", description = "Retrieves the recent notifications for a user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notifications retrieved successfully")
    })
    public ResponseEntity<List<NotificationDto>> getNotifications(
            @Parameter(description = "User account email address", required = true)
            @PathVariable("email") String email
    ) {
        List<NotificationDto> notifications = notificationService.getNotifications(email);
        return ResponseEntity.ok(notifications);
    }

    @PostMapping("/notifications/read/{email}")
    @Operation(summary = "Mark all notifications as read", description = "Marks all unread notifications for a user as read.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notifications marked as read successfully")
    })
    public ResponseEntity<MessageResponse> markNotificationsAsRead(
            @Parameter(description = "User account email address", required = true)
            @PathVariable("email") String email
    ) {
        MessageResponse response = notificationService.markNotificationsAsRead(email);
        return ResponseEntity.ok(response);
    }
}

