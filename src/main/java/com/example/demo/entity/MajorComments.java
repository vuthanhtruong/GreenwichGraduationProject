package com.example.demo.entity;

import com.example.demo.classPost.model.MajorClassPosts;
import com.example.demo.employe.model.MajorEmployes;
import com.example.demo.entity.AbstractClasses.Comments;
import com.example.demo.entity.Enums.Notifications;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "MajorComments")
@PrimaryKeyJoinColumn(name = "CommentID")
@Getter
@Setter
public class MajorComments extends Comments {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CommenterID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MajorEmployes commenter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PostID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MajorClassPosts post;

    public MajorComments() {}

    public MajorComments(String commentId, MajorEmployes commenter, MajorClassPosts post, Notifications notification, String content, LocalDateTime createdAt) {
        super(commentId, notification, content, createdAt);
        this.commenter = commenter;
        this.post = post;
    }
}