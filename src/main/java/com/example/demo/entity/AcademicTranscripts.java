package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "AcademicTranscripts")
@Getter
@Setter
public class AcademicTranscripts {

    @Id
    @Column(name = "TranscriptID", nullable = false)
    private String transcriptId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NumberID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Students student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SubjectID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Subjects subject;

    @Enumerated(EnumType.STRING)
    @Column(name = "Notification", nullable = true)
    private Notifications notification;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Marker", nullable = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Lecturers marker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Creator", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Staffs creator;

    @Column(name = "Grade", nullable = true, length = 10)
    @Enumerated(EnumType.STRING)
    private Grades grade;

    @Column(name = "Score", nullable = true)
    private Double score;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;

    public AcademicTranscripts() {}

    public AcademicTranscripts(String transcriptId, Students student, Subjects subject, Grades grade, LocalDateTime createdAt, Staffs creator) {
        this.transcriptId = transcriptId;
        this.student = student;
        this.subject = subject;
        this.grade = grade;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        if (creator == null) {
            throw new IllegalArgumentException("Creator cannot be null");
        }
        this.creator = creator;
    }
}