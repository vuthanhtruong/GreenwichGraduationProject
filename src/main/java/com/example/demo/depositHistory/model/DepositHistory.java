package com.example.demo.depositHistory.model;

import com.example.demo.accountBalance.model.AccountBalances;
import com.example.demo.entity.Enums.Status;
import com.example.demo.student.model.Students;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "DepositHistory")
@Getter
@Setter
public class DepositHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "DepositHistoryID", updatable = false, nullable = false)
    private UUID depositHistoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "StudentID", nullable = false)
    private Students student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AccountBalanceID", nullable = false)
    private AccountBalances accountBalance;

    @Column(name = "Amount", nullable = false)
    private Double amount;

    @Column(name = "DepositTime", nullable = false)
    private LocalDateTime depositTime;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status", nullable = false, length = 50)
    private Status status;

    @Column(name = "Description", length = 1000)
    private String description;

    @Version
    @Column(name = "version")
    private Long version;
}
