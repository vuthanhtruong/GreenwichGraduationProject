package com.example.demo.scholarship.model;

import com.example.demo.admin.model.Admins;
import com.example.demo.entity.Enums.Status;
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
@Table(name = "Scholarships")
@Getter
@Setter
public class Scholarships {

    @Id
    @Column(name = "ScholarshipID", nullable = false)
    private String scholarshipId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "StudentID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Students student;

    @Column(name = "TypeName", nullable = false, length = 255)
    private String typeName;

    @Column(name = "AwardDate", nullable = false)
    private LocalDate awardDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Creator", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Admins creator;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;

    public Scholarships() {
        this.createdAt = LocalDateTime.now();
    }

    public Scholarships(String scholarshipId, Students student, String typeName, Double amount, LocalDate awardDate, Admins creator, LocalDateTime createdAt) {
        this.scholarshipId = scholarshipId;
        this.student = student;
        this.typeName = typeName;
        this.awardDate = awardDate;
        this.creator = creator;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }
}