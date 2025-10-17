package com.example.demo.accountBalance.model;

import com.example.demo.user.student.model.Students;
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
    @Column(name = "StudentID", unique = true, nullable = false)
    private String studentId;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @MapsId
    @JoinColumn(name = "StudentID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Students student;

    @Column(name = "Balance", nullable = false)
    private Double balance;

    @Column(name = "LastUpdated", nullable = false)
    private LocalDateTime lastUpdated;

    public AccountBalances() {}

    public AccountBalances(Students student, Double balance, LocalDateTime lastUpdated) {
        this.student = student;
        this.balance = balance;
        this.lastUpdated = lastUpdated;
    }
}
