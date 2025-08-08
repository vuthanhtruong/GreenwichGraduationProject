package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "LecturerEvaluations")
@Getter
@Setter
public class MajorLecturerEvaluations {

    @Id
    @Column(name = "LecturerEvaluationID")
    private String lecturerEvaluationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ReviewerID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Students reviewer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LecturerID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MajorLecturers lecturer;

    @Enumerated(EnumType.STRING)
    @Column(name = "Notification", nullable = true)
    private Notifications notification;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ClassID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MajorClasses classEntity;

    @Column(name = "Text", nullable = true, length = 1000)
    private String text;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;

    public MajorLecturerEvaluations() {}

    public MajorLecturerEvaluations(String lecturerEvaluationId, Students reviewer, MajorLecturers lecturer, Notifications notification, MajorClasses classEntity, String text, LocalDateTime createdAt) {
        this.lecturerEvaluationId = lecturerEvaluationId;
        this.reviewer = reviewer;
        this.lecturer = lecturer;
        this.notification = notification;
        this.classEntity = classEntity;
        this.text = text;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }
}