package com.example.demo.dto;

import java.time.LocalDateTime;

public class NotificationsDTO {
    private String notificationId;
    private String title;
    private String description;
    private LocalDateTime notificationDate;
    private String notificationType;

    public NotificationsDTO(String notificationId, String title, String description,
                            LocalDateTime notificationDate, String notificationType) {
        this.notificationId = notificationId;
        this.title = title;
        this.description = description;
        this.notificationDate = notificationDate;
        this.notificationType = notificationType;
    }

    // Getters and Setters
    public String getNotificationId() { return notificationId; }
    public void setNotificationId(String notificationId) { this.notificationId = notificationId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDateTime getNotificationDate() { return notificationDate; }
    public void setNotificationDate(LocalDateTime notificationDate) { this.notificationDate = notificationDate; }
    public String getNotificationType() { return notificationType; }
    public void setNotificationType(String notificationType) { this.notificationType = notificationType; }
}