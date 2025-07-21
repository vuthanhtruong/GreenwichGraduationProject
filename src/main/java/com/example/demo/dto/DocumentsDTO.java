package com.example.demo.dto;

public class DocumentsDTO {
    private String documentId;
    private String creatorName;
    private String postId;
    private String documentTitle;
    private String filePath;

    public DocumentsDTO(String documentId, String creatorName, String postId, String documentTitle, String filePath) {
        this.documentId = documentId;
        this.creatorName = creatorName;
        this.postId = postId;
        this.documentTitle = documentTitle;
        this.filePath = filePath;
    }

    // Getters and Setters
    public String getDocumentId() { return documentId; }
    public void setDocumentId(String documentId) { this.documentId = documentId; }
    public String getCreatorName() { return creatorName; }
    public void setCreatorName(String creatorName) { this.creatorName = creatorName; }
    public String getPostId() { return postId; }
    public void setPostId(String postId) { this.postId = postId; }
    public String getDocumentTitle() { return documentTitle; }
    public void setDocumentTitle(String documentTitle) { this.documentTitle = documentTitle; }
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
}