package com.example.demo.entity.AbstractClasses;

import com.example.demo.entity.Enums.Notifications;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "Comments")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
public abstract class Comments {

    @Id
    @Column(name = "CommentID")
    private String commentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "Notification", nullable = true)
    private Notifications notification;

    @Column(name = "Content", nullable = true, length = 1000)
    private String content;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;

    public Comments() {}

    public Comments(String commentId, Notifications notification, String content, LocalDateTime createdAt) {
        this.commentId = commentId;
        this.notification = notification;
        this.content = content;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }
}