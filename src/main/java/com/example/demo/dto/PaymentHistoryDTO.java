package com.example.demo.dto;

import java.time.LocalDateTime;

public class PaymentHistoryDTO {
    private String paymentHistoryId;
    private String studentName;
    private String subjectName;
    private Double remainingBalance;
    private LocalDateTime paymentTime;
    private String status;

    public PaymentHistoryDTO(String paymentHistoryId, String studentName, String subjectName,
                             Double remainingBalance, LocalDateTime paymentTime, String status) {
        this.paymentHistoryId = paymentHistoryId;
        this.studentName = studentName;
        this.subjectName = subjectName;
        this.remainingBalance = remainingBalance;
        this.paymentTime = paymentTime;
        this.status = status;
    }

    // Getters and Setters
    public String getPaymentHistoryId() { return paymentHistoryId; }
    public void setPaymentHistoryId(String paymentHistoryId) { this.paymentHistoryId = paymentHistoryId; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
    public Double getRemainingBalance() { return remainingBalance; }
    public void setRemainingBalance(Double remainingBalance) { this.remainingBalance = remainingBalance; }
    public LocalDateTime getPaymentTime() { return paymentTime; }
    public void setPaymentTime(LocalDateTime paymentTime) { this.paymentTime = paymentTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}