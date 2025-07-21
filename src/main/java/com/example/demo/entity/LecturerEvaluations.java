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
public class LecturerEvaluations {

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
    private Lecturers lecturer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ReceiverID", nullable = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Staffs receiver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NotificationID", nullable = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Notifications notification;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ClassID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Classes classEntity;

    @Column(name = "Text", nullable = true, length = 1000)
    private String text;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;
}