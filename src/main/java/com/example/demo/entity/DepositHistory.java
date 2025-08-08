package com.example.demo.entity;

import com.example.demo.entity.Enums.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "DepositHistory")
@Getter
@Setter
public class DepositHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "DepositHistoryID")
    private String depositHistoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "StudentID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Students student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AccountBalanceID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private AccountBalances accountBalance;

    @Column(name = "Amount", nullable = false)
    private Double amount;

    @Column(name = "DepositTime", nullable = false)
    private LocalDateTime depositTime;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "Status", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "Description", nullable = true, length = 1000)
    private String description;

    // Constructors
    public DepositHistory() {}

    public DepositHistory(String depositHistoryId, Students student, AccountBalances accountBalance,
                          Double amount, LocalDateTime depositTime, LocalDateTime createdAt,
                          Status status, String description) {
        this.depositHistoryId = depositHistoryId;
        this.student = student;
        this.accountBalance = accountBalance;
        this.amount = amount;
        this.depositTime = depositTime;
        this.createdAt = createdAt;
        this.status = status;
        this.description = description;
    }
}