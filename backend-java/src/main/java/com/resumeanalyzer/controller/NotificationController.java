package com.resumeanalyzer.controller;

import com.resumeanalyzer.dto.MessageResponse;
import com.resumeanalyzer.dto.NotificationDto;
import com.resumeanalyzer.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/notifications/{email}")
    public ResponseEntity<List<NotificationDto>> getNotifications(@PathVariable("email") String email) {
        List<NotificationDto> notifications = notificationService.getNotifications(email);
        return ResponseEntity.ok(notifications);
    }

    @PostMapping("/notifications/read/{email}")
    public ResponseEntity<MessageResponse> markNotificationsAsRead(@PathVariable("email") String email) {
        MessageResponse response = notificationService.markNotificationsAsRead(email);
        return ResponseEntity.ok(response);
    }
}
