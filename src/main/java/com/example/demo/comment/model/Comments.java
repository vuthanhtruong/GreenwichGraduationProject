package com.example.demo.comment.model;

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
    @Column(name = "Notification")
    private Notifications notification;

    @Column(name = "Content", length = 1000)
    private String content;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;

    protected Comments() {}

    protected Comments(String commentId, Notifications notification, String content, LocalDateTime createdAt) {
        this.commentId = commentId;
        this.notification = notification;
        this.content = content;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    // 🔹 Abstract API để xoá instanceof
    public abstract String getCommenterId();
    public abstract String getCommenterName();
    public abstract Object getCommenterEntity();
    public abstract String getPostId();
    public abstract Object getPostEntity();
}
