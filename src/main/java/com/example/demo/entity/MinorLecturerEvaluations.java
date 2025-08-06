package com.example.demo.entity;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ReceiverID", nullable = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private DeputyStaffs receiver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NotificationID", nullable = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
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

    public MinorLecturerEvaluations(String minorLecturerEvaluationId, Students reviewer, MinorLecturers minorLecturer, DeputyStaffs receiver, Notifications notification, MinorClasses minorClass, String text, LocalDateTime createdAt) {
        this.minorLecturerEvaluationId = minorLecturerEvaluationId;
        this.reviewer = reviewer;
        this.minorLecturer = minorLecturer;
        this.receiver = receiver;
        this.notification = notification;
        this.minorClass = minorClass;
        this.text = text;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }
}