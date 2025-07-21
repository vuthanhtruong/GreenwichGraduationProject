package com.example.demo.dto;

import java.time.LocalDate;

public class EmployesDTO {
    private String id;
    private String fullName;
    private String email;
    private String majorName;
    private LocalDate createdDate;

    public EmployesDTO() {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.createdDate = createdDate;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public LocalDate getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDate createdDate) { this.createdDate = createdDate; }
    public String getMajorName() { return majorName; }
    public void setMajorName(String majorManagement) { this.majorName = majorName; }
}