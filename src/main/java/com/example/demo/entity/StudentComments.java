package com.example.demo.entity;

import com.example.demo.entity.AbstractClasses.ClassPosts;
import com.example.demo.entity.AbstractClasses.Comments;
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
@PrimaryKeyJoinColumn(name = "CommentID")
@Getter
@Setter
public class StudentComments extends Comments {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CommenterID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Students commenter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PostID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ClassPosts post;

    public StudentComments() {}

    public StudentComments(String commentId, Students commenter, ClassPosts post, Notifications notification, String content, LocalDateTime createdAt) {
        super(commentId, notification, content, createdAt);
        this.commenter = commenter;
        this.post = post;
    }
}