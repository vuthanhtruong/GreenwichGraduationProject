package com.example.demo.dto;

import java.time.LocalDateTime;

public class Students_ClassesDTO {
    private String studentId;
    private String classId;
    private String studentName;
    private String className;
    private LocalDateTime createdAt;

    public Students_ClassesDTO(String studentId, String classId, String studentName,
                               String className, LocalDateTime createdAt) {
        this.studentId = studentId;
        this.classId = classId;
        this.studentName = studentName;
        this.className = className;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public String getClassId() { return classId; }
    public void setClassId(String classId) { this.classId = classId; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}