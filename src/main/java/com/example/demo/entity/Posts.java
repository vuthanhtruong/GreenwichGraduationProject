package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "Posts")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
public class Posts {

    @Id
    @Column(name = "PostID")
    private String postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NotificationID", nullable = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Notifications notification;

    @Column(name = "Title", nullable = true, length = 255)
    private String title;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;
}