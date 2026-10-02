package com.resumeanalyzer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "message", nullable = false)
    private String message;

    @Column(name = "type")
    private String type;

    @Column(name = "is_read")
    private Integer isRead = 0;

    @Column(name = "created_at")
    private String createdAt;

    public Notification() {
    }

    public Notification(String email, String message, String type, Integer isRead, String createdAt) {
        this.email = email;
        this.message = message;
        this.type = type;
        this.isRead = isRead != null ? isRead : 0;
        this.createdAt = createdAt;
    }

    public Notification(Long id, String email, String message, String type, Integer isRead, String createdAt) {
        this.id = id;
        this.email = email;
        this.message = message;
        this.type = type;
        this.isRead = isRead != null ? isRead : 0;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getIsRead() {
        return isRead;
    }

    public void setIsRead(Integer isRead) {
        this.isRead = isRead;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
