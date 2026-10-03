package com.resumeanalyzer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public class NotificationDto {

    private String message;
    private String type;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("is_read")
    private Integer isRead;

    public NotificationDto() {
    }

    public NotificationDto(String message, String type, String createdAt, Integer isRead) {
        this.message = message;
        this.type = type;
        this.createdAt = createdAt;
        this.isRead = isRead;
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

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getIsRead() {
        return isRead;
    }

    public void setIsRead(Integer isRead) {
        this.isRead = isRead;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationDto that = (NotificationDto) o;
        return Objects.equals(message, that.message) &&
                Objects.equals(type, that.type) &&
                Objects.equals(createdAt, that.createdAt) &&
                Objects.equals(isRead, that.isRead);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, type, createdAt, isRead);
    }

    @Override
    public String toString() {
        return "NotificationDto{" +
                "message='" + message + '\'' +
                ", type='" + type + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", isRead=" + isRead +
                '}';
    }
}
