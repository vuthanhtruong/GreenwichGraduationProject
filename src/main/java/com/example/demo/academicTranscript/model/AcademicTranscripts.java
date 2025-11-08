package com.example.demo.academicTranscript.model;

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

    // === ĐIỂM CHÍNH ===
    @Column(name = "Score", nullable = true)
    private Double score;

    // === 3 TRƯỜNG ĐIỂM PHỤ THÊM ===
    @Column(name = "ScoreComponent1", nullable = true)
    private Double scoreComponent1; // Ví dụ: Điểm quá trình 1

    @Column(name = "ScoreComponent2", nullable = true)
    private Double scoreComponent2; // Ví dụ: Điểm giữa kỳ

    @Column(name = "ScoreComponent3", nullable = true)
    private Double scoreComponent3; // Ví dụ: Điểm thực hành / tiểu luận

    // === ĐIỂM CHỮ TỔNG (nếu cần) ===
    @Column(name = "Grade", nullable = true, length = 10)
    @Enumerated(EnumType.STRING)
    private Grades grade;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;

    // ABSTRACT METHOD: Returns subject name as String
    public abstract String getSubjectName();
    public abstract String getSubjectId();

    public AcademicTranscripts() {}

    public AcademicTranscripts(String transcriptId, Students student, Grades grade, LocalDateTime createdAt) {
        this.transcriptId = transcriptId;
        this.student = student;
        this.grade = grade;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }
}