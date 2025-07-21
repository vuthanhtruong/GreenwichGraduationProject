package com.example.demo.dto;

import java.time.LocalDateTime;

public class SubmissionsDTO {
    private String submissionId;
    private String submittedByName;
    private String assignmentSubmitSlotId;
    private LocalDateTime createdAt;

    public SubmissionsDTO(String submissionId, String submittedByName, String assignmentSubmitSlotId,
                          LocalDateTime createdAt) {
        this.submissionId = submissionId;
        this.submittedByName = submittedByName;
        this.assignmentSubmitSlotId = assignmentSubmitSlotId;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public String getSubmissionId() { return submissionId; }
    public void setSubmissionId(String submissionId) { this.submissionId = submissionId; }
    public String getSubmittedByName() { return submittedByName; }
    public void setSubmittedByName(String submittedByName) { this.submittedByName = submittedByName; }
    public String getAssignmentSubmitSlotId() { return assignmentSubmitSlotId; }
    public void setAssignmentSubmitSlotId(String assignmentSubmitSlotId) { this.assignmentSubmitSlotId = assignmentSubmitSlotId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}