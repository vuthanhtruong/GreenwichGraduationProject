package com.example.demo.entity.AbstractClasses;

import com.example.demo.entity.Enums.Notifications;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "PublicPosts")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
public abstract class PublicPosts {

    @Id
    @Column(name = "PostID")
    private String postId;

    @Enumerated(EnumType.STRING)
    @Column(name = "Notification", nullable = true)
    private Notifications notification;

    @Column(name = "Title", nullable = true, length = 255)
    private String title;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;

    public PublicPosts() {}

    public PublicPosts(String postId, Notifications notification, String title, LocalDateTime createdAt) {
        this.postId = postId;
        this.notification = notification;
        this.title = title;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }
}