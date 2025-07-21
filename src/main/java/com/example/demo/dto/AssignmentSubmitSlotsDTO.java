package com.example.demo.dto;

import java.time.LocalDateTime;

public class AssignmentSubmitSlotsDTO {
    private String postId;
    private String creatorName;
    private String className;
    private String content;
    private LocalDateTime deadline;

    public AssignmentSubmitSlotsDTO(String postId, String creatorName, String className,
                                    String content, LocalDateTime deadline) {
        this.postId = postId;
        this.creatorName = creatorName;
        this.className = className;
        this.content = content;
        this.deadline = deadline;
    }

    // Getters and Setters
    public String getPostId() { return postId; }
    public void setPostId(String postId) { this.postId = postId; }
    public String getCreatorName() { return creatorName; }
    public void setCreatorName(String creatorName) { this.creatorName = creatorName; }
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public LocalDateTime getDeadline() { return deadline; }
    public void setDeadline(LocalDateTime deadline) { this.deadline = deadline; }
}