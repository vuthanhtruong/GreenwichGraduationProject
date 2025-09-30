package com.example.demo.scholarshipByYear.model;

import com.example.demo.admin.model.Admins;
import com.example.demo.entity.Enums.ActivityStatus;
import com.example.demo.entity.Enums.ContractStatus;
import com.example.demo.scholarship.model.Scholarships;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "ScholarshipByYear")
@Getter
@Setter
public class ScholarshipByYear {

    @EmbeddedId
    private ScholarshipByYearId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("scholarshipId")
    @JoinColumn(name = "ScholarshipID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Scholarships scholarship;

    @Column(name = "Amount", nullable = false)
    private Double amount;

    @Column(name = "DiscountPercentage", nullable = true)
    private Double discountPercentage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Creator", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Admins creator;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status", nullable = false)
    private ActivityStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "ContractStatus", nullable = true)
    private ContractStatus contractStatus;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;

    public ScholarshipByYear() {
        this.createdAt = LocalDateTime.now();
    }

    public ScholarshipByYear(Scholarships scholarship, Integer admissionYear, Double amount, Double discountPercentage, Admins creator, ActivityStatus status) {
        this.id = new ScholarshipByYearId(scholarship.getScholarshipId(), admissionYear);
        this.scholarship = scholarship;
        this.amount = amount;
        this.discountPercentage = discountPercentage;
        this.creator = creator;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }
}