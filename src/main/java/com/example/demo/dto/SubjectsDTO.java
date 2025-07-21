package com.example.demo.dto;

public class SubjectsDTO {
    private String subjectId;
    private String subjectName;
    private Double tuition;
    private String majorName;

    public SubjectsDTO(String subjectId, String subjectName, Double tuition, String majorName) {
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.tuition = tuition;
        this.majorName = majorName;
    }

    // Getters and Setters
    public String getSubjectId() { return subjectId; }
    public void setSubjectId(String subjectId) { this.subjectId = subjectId; }
    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
    public Double getTuition() { return tuition; }
    public void setTuition(Double tuition) { this.tuition = tuition; }
    public String getMajorName() { return majorName; }
    public void setMajorName(String majorName) { this.majorName = majorName; }
}