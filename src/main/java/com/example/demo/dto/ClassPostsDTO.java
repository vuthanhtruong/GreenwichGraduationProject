package com.example.demo.dto;

import java.time.LocalDateTime;

public class ClassPostsDTO {
    private String postId;
    private String creatorName;
    private String className;
    private String title;
    private String content;
    private LocalDateTime createdAt;

    public ClassPostsDTO(String postId, String creatorName, String className, String title,
                         String content, LocalDateTime createdAt) {
        this.postId = postId;
        this.creatorName = creatorName;
        this.className = className;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public String getPostId() { return postId; }
    public void setPostId(String postId) { this.postId = postId; }
    public String getCreatorName() { return creatorName; }
    public void setCreatorName(String creatorName) { this.creatorName = creatorName; }
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}