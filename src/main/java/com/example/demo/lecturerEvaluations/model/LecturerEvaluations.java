// src/main/java/com/example/demo/entity/LecturerEvaluations.java
package com.example.demo.lecturerEvaluations.model;

import com.example.demo.classes.abstractClasses.model.Classes;
import com.example.demo.user.student.model.Students;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "LecturerEvaluations")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "EvaluationType", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
public abstract class LecturerEvaluations {

    @Id
    @Column(name = "EvaluationID")
    private String evaluationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ReviewerID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Students reviewer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ClassID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Classes classEntity;

    @Column(name = "Text", nullable = true, length = 1000)
    private String text;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public abstract String getLecturerId();

    // Constructor
    protected LecturerEvaluations() {}

    protected LecturerEvaluations(String evaluationId, Students reviewer, Classes classEntity, String text) {
        this.evaluationId = evaluationId;
        this.reviewer = reviewer;
        this.classEntity = classEntity;
        this.text = text;
        this.createdAt = LocalDateTime.now();
    }
}