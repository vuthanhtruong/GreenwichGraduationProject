package com.example.demo.dto;

import java.time.LocalDateTime;

public class PasswordResetTokenDTO {
    private String id;
    private String personName;
    private String otp;
    private LocalDateTime expiryDate;

    public PasswordResetTokenDTO(String id, String personName, String otp, LocalDateTime expiryDate) {
        this.id = id;
        this.personName = personName;
        this.otp = otp;
        this.expiryDate = expiryDate;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getPersonName() { return personName; }
    public void setPersonName(String personName) { this.personName = personName; }
    public String getOtp() { return otp; }
    public void setOtp(String otp) { this.otp = otp; }
    public LocalDateTime getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDateTime expiryDate) { this.expiryDate = expiryDate; }
}