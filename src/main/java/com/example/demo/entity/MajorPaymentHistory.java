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
public class MajorPaymentHistory extends PaymentHistory {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SubjectID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Subjects subject;

    public MajorPaymentHistory() {}

    public MajorPaymentHistory(String paymentHistoryId, Students student, Subjects subject, AccountBalances accountBalance,
                               LocalDateTime paymentTime, LocalDateTime createdAt, Status status) {
        super(paymentHistoryId, student, accountBalance, paymentTime, createdAt, status);
        this.subject = subject;
    }
}