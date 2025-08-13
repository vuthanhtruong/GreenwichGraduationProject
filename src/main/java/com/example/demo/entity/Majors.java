package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@Entity
@Table(name = "Majors")
@Getter
@Setter
public class Majors {

    @Id
    @Column(name = "MajorID")
    private String majorId;

    @Column(name = "MajorName", nullable = false, length = 255)
    private String majorName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CreatorID", nullable = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Admins creator;

    @Column(name = "CreatedDate", nullable = false, updatable = false)
    private LocalDate createdDate = LocalDate.now();
}