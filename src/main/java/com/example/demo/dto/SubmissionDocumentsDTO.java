package com.example.demo.dto;

public class SubmissionDocumentsDTO {
    private String submissionDocumentId;
    private String submissionId;
    private String creatorName;
    private String filePath;

    public SubmissionDocumentsDTO(String submissionDocumentId, String submissionId,
                                  String creatorName, String filePath) {
        this.submissionDocumentId = submissionDocumentId;
        this.submissionId = submissionId;
        this.creatorName = creatorName;
        this.filePath = filePath;
    }

    // Getters and Setters
    public String getSubmissionDocumentId() { return submissionDocumentId; }
    public void setSubmissionDocumentId(String submissionDocumentId) { this.submissionDocumentId = submissionDocumentId; }
    public String getSubmissionId() { return submissionId; }
    public void setSubmissionId(String submissionId) { this.submissionId = submissionId; }
    public String getCreatorName() { return creatorName; }
    public void setCreatorName(String creatorName) { this.creatorName = creatorName; }
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
}