package com.example.demo.entity;

import com.example.demo.employe.model.MinorEmployes;
import com.example.demo.entity.AbstractClasses.Comments;
import com.example.demo.entity.Enums.Notifications;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "MinorComments")
@PrimaryKeyJoinColumn(name = "CommentID")
@Getter
@Setter
public class MinorComments extends Comments {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CommenterID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MinorEmployes commenter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PostID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MinorClassPosts post;

    public MinorComments() {}

    public MinorComments(String commentId, MinorEmployes commenter, MinorClassPosts post, Notifications notification, String content, LocalDateTime createdAt) {
        super(commentId, notification, content, createdAt);
        this.commenter = commenter;
        this.post = post;
    }
}