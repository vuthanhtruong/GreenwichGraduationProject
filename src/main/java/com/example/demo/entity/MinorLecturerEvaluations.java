package com.example.demo.entity;

import com.example.demo.classes.minorClasses.model.MinorClasses;
import com.example.demo.entity.Enums.Notifications;
import com.example.demo.user.minorLecturer.model.MinorLecturers;
import com.example.demo.user.student.model.Students;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "MinorLecturerEvaluations")
@Getter
@Setter
public class MinorLecturerEvaluations {

    @Id
    @Column(name = "MinorLecturerEvaluationID")
    private String minorLecturerEvaluationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ReviewerID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Students reviewer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MinorLecturerID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MinorLecturers minorLecturer;

    @Enumerated(EnumType.STRING)
    @Column(name = "Notification", nullable = true)
    private Notifications notification;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MinorClassID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MinorClasses minorClass;

    @Column(name = "Text", nullable = true, length = 1000)
    private String text;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;

    public MinorLecturerEvaluations() {}

    public MinorLecturerEvaluations(String minorLecturerEvaluationId, Students reviewer, MinorLecturers minorLecturer, Notifications notification, MinorClasses minorClass, String text, LocalDateTime createdAt) {
        this.minorLecturerEvaluationId = minorLecturerEvaluationId;
        this.reviewer = reviewer;
        this.minorLecturer = minorLecturer;
        this.notification = notification;
        this.minorClass = minorClass;
        this.text = text;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }
}