package com.example.demo.classPost.model;

import com.example.demo.entity.Enums.Notifications;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "ClassPosts")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
public abstract class ClassPosts {

    @Id
    @Column(name = "PostID")
    private String postId;

    @Enumerated(EnumType.STRING)
    @Column(name = "Notification", nullable = true)
    private Notifications notification;

    @Column(name = "Content", nullable = true, length = 1000)
    private String content;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;

    public ClassPosts() {}

    public ClassPosts(String postId, Notifications notification, String content, LocalDateTime createdAt) {
        this.postId = postId;
        this.notification = notification;
        this.content = content;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }
}