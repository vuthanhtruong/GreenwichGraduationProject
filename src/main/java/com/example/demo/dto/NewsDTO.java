package com.example.demo.dto;

import java.time.LocalDateTime;

public class NewsDTO {
    private String postId;
    private String creatorName;
    private String title;
    private String content;
    private LocalDateTime createdAt;

    public NewsDTO(String postId, String creatorName, String title, String content, LocalDateTime createdAt) {
        this.postId = postId;
        this.creatorName = creatorName;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public String getPostId() { return postId; }
    public void setPostId(String postId) { this.postId = postId; }
    public String getCreatorName() { return creatorName; }
    public void setCreatorName(String creatorName) { this.creatorName = creatorName; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}