package com.example.demo.dto;

import java.time.LocalDateTime;

public class ClassesDTO {
    private String classId;
    private String nameClass;
    private Integer slotQuantity;
    private String subjectName;
    private LocalDateTime createdAt;

    public ClassesDTO(String classId, String nameClass, Integer slotQuantity, String subjectName, LocalDateTime createdAt) {
        this.classId = classId;
        this.nameClass = nameClass;
        this.slotQuantity = slotQuantity;
        this.subjectName = subjectName;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public String getClassId() { return classId; }
    public void setClassId(String classId) { this.classId = classId; }
    public String getNameClass() { return nameClass; }
    public void setNameClass(String nameClass) { this.nameClass = nameClass; }
    public Integer getSlotQuantity() { return slotQuantity; }
    public void setSlotQuantity(Integer slotQuantity) { this.slotQuantity = slotQuantity; }
    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}