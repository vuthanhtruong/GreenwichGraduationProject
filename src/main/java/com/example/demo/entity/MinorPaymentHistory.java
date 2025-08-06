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
public class MinorPaymentHistory extends PaymentHistory {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MinorSubjectID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MinorSubjects minorSubject;

    public MinorPaymentHistory() {}

    public MinorPaymentHistory(String minorPaymentHistoryId, Students student, MinorSubjects minorSubject, AccountBalances accountBalance,
                               LocalDateTime paymentTime, LocalDateTime createdAt, Status status) {
        super(minorPaymentHistoryId, student, accountBalance, paymentTime, createdAt, status);
        this.minorSubject = minorSubject;
    }
}