package com.example.demo.dto;

public class SyllabusesDTO {
    private String syllabusId;
    private String syllabusName;
    private String subjectName;
    private String filePath;
    private String status;

    public SyllabusesDTO(String syllabusId, String syllabusName, String subjectName,
                         String filePath, String status) {
        this.syllabusId = syllabusId;
        this.syllabusName = syllabusName;
        this.subjectName = subjectName;
        this.filePath = filePath;
        this.status = status;
    }

    // Getters and Setters
    public String getSyllabusId() { return syllabusId; }
    public void setSyllabusId(String syllabusId) { this.syllabusId = syllabusId; }
    public String getSyllabusName() { return syllabusName; }
    public void setSyllabusName(String syllabusName) { this.syllabusName = syllabusName; }
    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}