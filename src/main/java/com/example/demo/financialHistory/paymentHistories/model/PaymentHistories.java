package com.example.demo.financialHistory.paymentHistories.model;

import com.example.demo.accountBalance.model.AccountBalances;
import com.example.demo.financialHistory.financialHistories.model.FinancialHistories;
import com.example.demo.entity.Enums.Status;
import com.example.demo.user.student.model.Students;
import com.example.demo.subject.abstractSubject.model.Subjects;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "PaymentHistories")
@PrimaryKeyJoinColumn(name = "HistoryID")
@Getter
@Setter
public class PaymentHistories extends FinancialHistories {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SubjectID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Subjects subject;

    @Column(name = "PaymentTime", nullable = false)
    private LocalDateTime paymentTime;

    public PaymentHistories() {}

    public PaymentHistories(String historyId, Students student, Subjects subject, AccountBalances accountBalance,
                            LocalDateTime paymentTime, BigDecimal currentAmount, LocalDateTime createdAt, Status status) {
        super(historyId, student, accountBalance, currentAmount,createdAt, status);
        this.subject = subject;
        this.paymentTime = paymentTime;
    }
}