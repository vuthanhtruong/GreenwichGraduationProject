package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "Subjects")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "subject_type", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
public abstract class Subjects {

    @Id
    @Column(name = "SubjectID")
    private String subjectId;

    @Column(name = "SubjectName", nullable = false, length = 255)
    private String subjectName;

    @Column(name = "Tuition", nullable = true)
    private Double tuition;

    @Column(name = "Semester", nullable = true)
    private Integer semester;

    @Enumerated(EnumType.STRING)
    @Column(name = "RequirementType", nullable = false)
    private SubjectType requirementType;
}