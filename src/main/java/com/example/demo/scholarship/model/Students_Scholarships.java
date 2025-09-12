package com.example.demo.scholarship.model;

import com.example.demo.entity.Enums.ActivityStatus;
import com.example.demo.student.model.Students;
import com.example.demo.staff.model.Staffs;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "Students_Scholarships")
@Getter
@Setter
public class Students_Scholarships {

    @Id
    @Column(name = "StudentID", nullable = false)
    private String studentId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "StudentID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Students student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ScholarshipID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Scholarships scholarship;

    @Column(name = "Amount", nullable = false)
    private Double amount;

    @Column(name = "AwardDate", nullable = false)
    private LocalDate awardDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Creator", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Staffs creator;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status", nullable = false, length = 50)
    private ActivityStatus status;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;

    public Students_Scholarships() {
        this.createdAt = LocalDateTime.now();
    }

    public Students_Scholarships(String studentId, Students student, Scholarships scholarship, Double amount, LocalDate awardDate, Staffs creator, ActivityStatus status, LocalDateTime createdAt) {
        this.studentId = studentId;
        this.student = student;
        this.scholarship = scholarship;
        this.amount = amount;
        this.awardDate = awardDate;
        this.creator = creator;
        this.status = status;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }
}