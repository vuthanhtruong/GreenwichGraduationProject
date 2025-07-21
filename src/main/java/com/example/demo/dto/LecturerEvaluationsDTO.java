package com.example.demo.dto;

import java.time.LocalDateTime;

public class LecturerEvaluationsDTO {
    private String lecturerEvaluationId;
    private String reviewerName;
    private String lecturerName;
    private String className;
    private String text;
    private LocalDateTime createdAt;

    public LecturerEvaluationsDTO(String lecturerEvaluationId, String reviewerName, String lecturerName,
                                  String className, String text, LocalDateTime createdAt) {
        this.lecturerEvaluationId = lecturerEvaluationId;
        this.reviewerName = reviewerName;
        this.lecturerName = lecturerName;
        this.className = className;
        this.text = text;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public String getLecturerEvaluationId() { return lecturerEvaluationId; }
    public void setLecturerEvaluationId(String lecturerEvaluationId) { this.lecturerEvaluationId = lecturerEvaluationId; }
    public String getReviewerName() { return reviewerName; }
    public void setReviewerName(String reviewerName) { this.reviewerName = reviewerName; }
    public String getLecturerName() { return lecturerName; }
    public void setLecturerName(String lecturerName) { this.lecturerName = lecturerName; }
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}