package com.example.demo.dto;

import java.time.LocalDateTime;

public class Lecturers_ClassesDTO {
    private String lecturerId;
    private String classId;
    private String lecturerName;
    private String className;
    private LocalDateTime createdAt;

    public Lecturers_ClassesDTO(String lecturerId, String classId, String lecturerName,
                                String className, LocalDateTime createdAt) {
        this.lecturerId = lecturerId;
        this.classId = classId;
        this.lecturerName = lecturerName;
        this.className = className;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public String getLecturerId() { return lecturerId; }
    public void setLecturerId(String lecturerId) { this.lecturerId = lecturerId; }
    public String getClassId() { return classId; }
    public void setClassId(String classId) { this.classId = classId; }
    public String getLecturerName() { return lecturerName; }
    public void setLecturerName(String lecturerName) { this.lecturerName = lecturerName; }
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}