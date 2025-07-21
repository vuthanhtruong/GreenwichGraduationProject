package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;


@Entity
@Table(name = "AccountBalance")
@PrimaryKeyJoinColumn(name = "StudentID")
@Getter
@Setter
public class AccountBalance extends Students {
    @Column(name = "AccountBalance", nullable = false)
    private Double accountBalance;
}