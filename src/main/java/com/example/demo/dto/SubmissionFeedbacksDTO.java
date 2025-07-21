package com.example.demo.dto;

import java.time.LocalDateTime;

public class SubmissionFeedbacksDTO {
    private String postId;
    private String announcerName;
    private String submissionId;
    private String content;
    private Double grade;
    private LocalDateTime createdAt;

    public SubmissionFeedbacksDTO(String postId, String announcerName, String submissionId,
                                  String content, Double grade, LocalDateTime createdAt) {
        this.postId = postId;
        this.announcerName = announcerName;
        this.submissionId = submissionId;
        this.content = content;
        this.grade = grade;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public String getPostId() { return postId; }
    public void setPostId(String postId) { this.postId = postId; }
    public String getAnnouncerName() { return announcerName; }
    public void setAnnouncerName(String announcerName) { this.announcerName = announcerName; }
    public String getSubmissionId() { return submissionId; }
    public void setSubmissionId(String submissionId) { this.submissionId = submissionId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Double getGrade() { return grade; }
    public void setGrade(Double grade) { this.grade = grade; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}