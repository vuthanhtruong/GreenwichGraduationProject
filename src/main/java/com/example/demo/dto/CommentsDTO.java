package com.example.demo.dto;

import java.time.LocalDateTime;

public class CommentsDTO {
    private String commentId;
    private String commenterName;
    private String postId;
    private String content;
    private LocalDateTime createdAt;

    public CommentsDTO(String commentId, String commenterName, String postId, String content, LocalDateTime createdAt) {
        this.commentId = commentId;
        this.commenterName = commenterName;
        this.postId = postId;
        this.content = content;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public String getCommentId() { return commentId; }
    public void setCommentId(String commentId) { this.commentId = commentId; }
    public String getCommenterName() { return commenterName; }
    public void setCommenterName(String commenterName) { this.commenterName = commenterName; }
    public String getPostId() { return postId; }
    public void setPostId(String postId) { this.postId = postId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}