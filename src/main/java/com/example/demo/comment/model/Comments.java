package com.example.demo.comment.model;

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

    @Column(name = "Content", length = 1000)
    private String content;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;

    protected Comments() {}

    protected Comments(String commentId, String content, LocalDateTime createdAt) {
        this.commentId = commentId;
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
