package com.example.demo.entity.AbstractClasses;

import com.example.demo.accountBalance.model.AccountBalances;
import com.example.demo.entity.Enums.Status;
import com.example.demo.user.student.model.Students;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "FinancialHistories")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
public abstract class FinancialHistories {

    @Id
    @Column(name = "HistoryID")
    private String historyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "StudentID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Students student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AccountBalanceID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private AccountBalances accountBalance;

    @Column(name = "CurrentAmount", nullable = false, precision = 15, scale = 2)
    private BigDecimal currentAmount;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status", nullable = false, length = 50)
    private Status status;

    @Version
    @Column(name = "version")
    private Long version;

    public FinancialHistories() {}

    public FinancialHistories(String historyId, Students student, AccountBalances accountBalance, BigDecimal currentAmount, LocalDateTime createdAt, Status status) {
        this.historyId = historyId;
        this.student = student;
        this.accountBalance = accountBalance;
        this.currentAmount = currentAmount != null ? currentAmount : BigDecimal.ZERO;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.status = status;
        this.version = 0L;
    }
}