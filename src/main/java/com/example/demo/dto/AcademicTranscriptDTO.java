package com.example.demo.dto;

import java.time.LocalDateTime;

public class AcademicTranscriptDTO {
    private String studentId;
    private String subjectId;
    private String studentName;
    private String subjectName;
    private Double score;
    private String grade;
    private String status;
    private LocalDateTime createdAt;

    public AcademicTranscriptDTO(String studentId, String subjectId, String studentName, String subjectName,
                                 Double score, String grade, String status, LocalDateTime createdAt) {
        this.studentId = studentId;
        this.subjectId = subjectId;
        this.studentName = studentName;
        this.subjectName = subjectName;
        this.score = score;
        this.grade = grade;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public String getSubjectId() { return subjectId; }
    public void setSubjectId(String subjectId) { this.subjectId = subjectId; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }
    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}