package com.example.demo.history.depositHistory.model;

import com.example.demo.accountBalance.model.AccountBalances;
import com.example.demo.entity.AbstractClasses.FinancialHistories;
import com.example.demo.entity.Enums.Status;
import com.example.demo.user.student.model.Students;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "DepositHistory")
@PrimaryKeyJoinColumn(name = "HistoryID")
@Getter
@Setter
public class DepositHistories extends FinancialHistories {

    @Column(name = "Amount", nullable = false)
    private Double amount;

    @Column(name = "DepositTime", nullable = false)
    private LocalDateTime depositTime;

    @Column(name = "Description", length = 1000)
    private String description;

    public DepositHistories() {}

    public DepositHistories(String historyId, Students student, AccountBalances accountBalance, Double amount,
                            LocalDateTime depositTime, BigDecimal currentAmount, LocalDateTime createdAt, Status status, String description) {
        super(historyId, student, accountBalance, currentAmount,createdAt, status);
        this.amount = amount;
        this.depositTime = depositTime;
        this.description = description;
    }
}