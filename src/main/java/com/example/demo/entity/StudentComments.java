package com.example.demo.entity;

import com.example.demo.entity.AbstractClasses.ClassPosts;
import com.example.demo.entity.Enums.Notifications;
import com.example.demo.student.model.Students;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "StudentComments")
@Getter
@Setter
public class StudentComments {

    @Id
    @Column(name = "CommentID")
    private String commentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CommenterID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Students commenter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PostID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ClassPosts post;

    @Enumerated(EnumType.STRING)
    @Column(name = "Notification", nullable = true)
    private Notifications notification;

    @Column(name = "Content", nullable = true, length = 1000)
    private String content;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;

    public StudentComments() {}

    public StudentComments(String commentId, Students commenter, ClassPosts post, Notifications notification, String content, LocalDateTime createdAt) {
        this.commentId = commentId;
        this.commenter = commenter;
        this.post = post;
        this.notification = notification;
        this.content = content;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }
}