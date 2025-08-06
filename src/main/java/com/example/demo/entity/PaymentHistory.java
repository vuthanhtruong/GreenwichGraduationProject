package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "PaymentHistory")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
public abstract class PaymentHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "PaymentHistoryID")
    private String paymentHistoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "StudentID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Students student;

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

    public PaymentHistory() {}

    public PaymentHistory(String paymentHistoryId, Students student, AccountBalances accountBalance,
                          LocalDateTime paymentTime, LocalDateTime createdAt, Status status) {
        this.paymentHistoryId = paymentHistoryId;
        this.student = student;
        this.accountBalance = accountBalance;
        this.paymentTime = paymentTime;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.status = status;
    }
}