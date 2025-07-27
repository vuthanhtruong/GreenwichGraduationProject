package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "AccountBalances")
@Getter
@Setter
public class AccountBalances {

    @Id
    @Column(name = "StudentID")
    private String studentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "StudentID", nullable = false, insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Students student;

    @Column(name = "Balance", nullable = false)
    private Double balance;

    @Column(name = "LastUpdated", nullable = false)
    private LocalDateTime lastUpdated;

    // Constructors
    public AccountBalances() {}

    public AccountBalances(String studentId, Students student, Double balance, LocalDateTime lastUpdated) {
        this.studentId = studentId;
        this.student = student;
        this.balance = balance;
        this.lastUpdated = lastUpdated;
    }
}