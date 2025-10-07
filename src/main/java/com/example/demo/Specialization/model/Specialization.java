package com.example.demo.Specialization.model;

import com.example.demo.major.model.Majors;
import com.example.demo.admin.model.Admins;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "Specializations")
@Getter
@Setter
public class Specialization {

    @Id
    @Column(name = "SpecializationID", nullable = false)
    private String specializationId;

    @Column(name = "SpecializationName", nullable = false, length = 255)
    private String specializationName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MajorID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Majors major;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CreatorID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Admins creator;

    @Column(name = "CreatedAt", nullable = true, updatable = false)
    private LocalDateTime createdAt;

    public Specialization() {
        this.createdAt = LocalDateTime.now();
    }

    public Specialization(String specializationId, String specializationName, Majors major, Admins creator) {
        this.specializationId = specializationId;
        this.specializationName = specializationName;
        this.major = major;
        this.creator = creator;
        this.createdAt = LocalDateTime.now();
    }
}