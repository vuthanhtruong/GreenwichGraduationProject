package com.example.demo.dto;

import java.time.LocalDateTime;

public class PostsDTO {
    private String postId;
    private String title;
    private LocalDateTime createdAt;

    public PostsDTO(String postId, String title, LocalDateTime createdAt) {
        this.postId = postId;
        this.title = title;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public String getPostId() { return postId; }
    public void setPostId(String postId) { this.postId = postId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}