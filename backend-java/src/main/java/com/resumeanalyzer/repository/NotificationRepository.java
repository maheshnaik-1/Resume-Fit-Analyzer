package com.resumeanalyzer.repository;

import com.resumeanalyzer.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Retrieves the latest 10 notifications for a user ordered by created_at descending.
     * Exact V1.0 Python equivalent:
     * SELECT message, type, created_at, is_read FROM notifications WHERE email = ? ORDER BY created_at DESC LIMIT 10
     */
    List<Notification> findTop10ByEmailOrderByCreatedAtDesc(String email);

    /**
     * Marks all notifications as read for a given user email.
     * Exact V1.0 Python equivalent:
     * UPDATE notifications SET is_read = 1 WHERE email = ?
     */
    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.isRead = 1 WHERE n.email = :email")
    int markAllAsReadByEmail(@Param("email") String email);
}
