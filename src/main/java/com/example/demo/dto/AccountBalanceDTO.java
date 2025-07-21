package com.example.demo.dto;

public class AccountBalanceDTO {
    private String studentId;
    private String studentName;
    private Double accountBalance;

    public AccountBalanceDTO(String studentId, String studentName, Double accountBalance) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.accountBalance = accountBalance;
    }

    // Getters and Setters
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public Double getAccountBalance() { return accountBalance; }
    public void setAccountBalance(Double accountBalance) { this.accountBalance = accountBalance; }
}