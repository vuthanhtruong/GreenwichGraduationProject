package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "Notifications")
@Getter
@Setter
public class Notifications {

    @Id
    @Column(name = "NotificationID")
    private String notificationId;

    @Column(name = "Title", nullable = true, length = 255)
    private String title;

    @Column(name = "Description", nullable = true, length = 1000)
    private String description;

    @Column(name = "NotificationDate", nullable = false)
    private LocalDateTime notificationDate;

    @Column(name = "NotificationType", nullable = true, length = 50)
    private String notificationType;


}