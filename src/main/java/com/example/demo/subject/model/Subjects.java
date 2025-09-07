package com.example.demo.subject.model;

import com.example.demo.admin.model.Admins;
import com.example.demo.entity.Enums.SubjectTypes;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "Subjects")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
public abstract class Subjects {

    @Id
    @Column(name = "SubjectID")
    private String subjectId;

    @Column(name = "SubjectName", nullable = false, length = 255)
    private String subjectName;

    @Column(name = "Semester", nullable = true)
    private Integer semester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AcceptorID", nullable = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Admins acceptor;

    @Enumerated(EnumType.STRING)
    @Column(name = "RequirementType", nullable = true)
    private SubjectTypes requirementType;
}