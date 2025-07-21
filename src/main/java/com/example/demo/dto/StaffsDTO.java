package com.example.demo.dto;

public class StaffsDTO {
    private String id;
    private String fullName;
    private String email;
    private String campusName;

    public StaffsDTO() {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.campusName = campusName;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getCampusName() { return campusName; }
    public void setCampusName(String campusName) { this.campusName = campusName; }
}