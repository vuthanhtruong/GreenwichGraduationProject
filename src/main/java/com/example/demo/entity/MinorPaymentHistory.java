package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "MinorPaymentHistory")
@Getter
@Setter
public class MinorPaymentHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "MinorPaymentHistoryID")
    private String minorPaymentHistoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "StudentID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Students student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MinorSubjectID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MinorSubjects minorSubject;

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

    // Constructors
    public MinorPaymentHistory() {}

    public MinorPaymentHistory(String minorPaymentHistoryId, Students student, MinorSubjects minorSubject, AccountBalances accountBalance,
                               LocalDateTime paymentTime, LocalDateTime createdAt, Status status) {
        this.minorPaymentHistoryId = minorPaymentHistoryId;
        this.student = student;
        this.minorSubject = minorSubject;
        this.accountBalance = accountBalance;
        this.paymentTime = paymentTime;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.status = status;
    }
}