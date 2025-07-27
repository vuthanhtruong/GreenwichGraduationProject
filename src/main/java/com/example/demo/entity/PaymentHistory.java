package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "PaymentHistory")
@Getter
@Setter
public class PaymentHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "PaymentHistoryID")
    private String paymentHistoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "StudentID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Students student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SubjectID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Subjects subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RemainingAccountBalance", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private AccountBalances accountBalance;

    @Column(name = "PaymentTime", nullable = false)
    private LocalDateTime paymentTime;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "Status", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status {
        SUCCESS, PENDING, FAILED
    }

    // Constructors
    public PaymentHistory() {}

    public PaymentHistory(String paymentHistoryId, Students student, Subjects subject, AccountBalances accountBalance,
                          LocalDateTime paymentTime, LocalDateTime createdAt, Status status) {
        this.paymentHistoryId = paymentHistoryId;
        this.student = student;
        this.subject = subject;
        this.accountBalance = accountBalance;
        this.paymentTime = paymentTime;
        this.createdAt = createdAt;
        this.status = status;
    }
}