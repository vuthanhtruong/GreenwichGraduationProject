package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "MinorSubjects")
@Getter
@Setter
public class MinorSubjects {

    @Id
    @Column(name = "MinorSubjectID")
    private String minorSubjectId;

    @Column(name = "MinorSubjectName", nullable = false, length = 255)
    private String minorSubjectName;

    @Column(name = "Tuition", nullable = true)
    private Double tuition;

    @Column(name = "Semester", nullable = true)
    private Integer semester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Creator", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private DeputyStaffs creator;

    public MinorSubjects() {}

    public MinorSubjects(String minorSubjectId, String minorSubjectName, Double tuition, Integer semester, DeputyStaffs creator) {
        this.minorSubjectId = minorSubjectId;
        this.minorSubjectName = minorSubjectName;
        this.tuition = tuition;
        this.semester = semester;
        this.creator = creator;
    }
}