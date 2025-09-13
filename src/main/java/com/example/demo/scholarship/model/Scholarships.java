package com.example.demo.scholarship.model;

import com.example.demo.admin.model.Admins;
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
    @Column(name = "ScholarshipID", nullable = true)
    private String scholarshipId;

    @Column(name = "TypeName", nullable = true, length = 255)
    private String typeName;

    @Column(name = "AwardDate", nullable = true)
    private LocalDate awardDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Creator", nullable = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Admins creator;

    @Column(name = "CreatedAt", nullable = true)
    private LocalDateTime createdAt;

    public Scholarships() {
        this.createdAt = LocalDateTime.now();
    }

    public Scholarships(String scholarshipId, String typeName, Double amount, LocalDate awardDate, Admins creator, LocalDateTime createdAt) {
        this.scholarshipId = scholarshipId;
        this.typeName = typeName;
        this.awardDate = awardDate;
        this.creator = creator;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }
}