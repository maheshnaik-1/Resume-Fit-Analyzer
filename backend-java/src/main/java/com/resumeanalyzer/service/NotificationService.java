package com.resumeanalyzer.service;

import com.resumeanalyzer.dto.MessageResponse;
import com.resumeanalyzer.dto.NotificationDto;
import com.resumeanalyzer.entity.Notification;
import com.resumeanalyzer.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public List<NotificationDto> getNotifications(String email) {
        if (email == null) {
            return Collections.emptyList();
        }
        String normalizedEmail = email.trim().toLowerCase();
        List<Notification> records = notificationRepository.findTop10ByEmailOrderByCreatedAtDesc(normalizedEmail);
        return records.stream()
                .map(n -> new NotificationDto(
                        n.getMessage(),
                        n.getType(),
                        n.getCreatedAt(),
                        n.getIsRead() != null ? n.getIsRead() : 0
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public MessageResponse markNotificationsAsRead(String email) {
        if (email != null) {
            String normalizedEmail = email.trim().toLowerCase();
            notificationRepository.markAllAsReadByEmail(normalizedEmail);
        }
        return new MessageResponse("Notifications marked as read");
    }
}
