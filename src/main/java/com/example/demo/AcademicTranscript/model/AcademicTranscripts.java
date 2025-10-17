package com.example.demo.AcademicTranscript.model;

import com.example.demo.entity.Enums.Grades;
import com.example.demo.entity.Enums.Notifications;
import com.example.demo.user.student.model.Students;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "AcademicTranscripts")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
public abstract class AcademicTranscripts {

    @Id
    @Column(name = "TranscriptID", nullable = false)
    private String transcriptId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NumberID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Students student;

    @Enumerated(EnumType.STRING)
    @Column(name = "Notification", nullable = true)
    private Notifications notification;

    @Column(name = "Grade", nullable = true, length = 10)
    @Enumerated(EnumType.STRING)
    private Grades grade;

    @Column(name = "Score", nullable = true)
    private Double score;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;

    public AcademicTranscripts() {}

    public AcademicTranscripts(String transcriptId, Students student, Grades grade, LocalDateTime createdAt) {
        this.transcriptId = transcriptId;
        this.student = student;
        this.grade = grade;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }
}