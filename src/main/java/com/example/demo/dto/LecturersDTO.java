package com.example.demo.dto;

public class LecturersDTO {
    private String id;
    private String fullName;
    private String email;
    private String type;

    public LecturersDTO(String id, String fullName, String email, String type) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.type = type;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}