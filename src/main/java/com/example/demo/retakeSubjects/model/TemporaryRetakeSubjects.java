package com.example.demo.retakeSubjects.model;

import com.example.demo.user.student.model.Students;
import com.example.demo.subject.abstractSubject.model.Subjects;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "TemporaryRetakeSubjects")  // Tên bảng mới trong DB
@Getter
@Setter
public class TemporaryRetakeSubjects {

    @EmbeddedId
    private RetakeSubjectsId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("studentId")
    @JoinColumn(name = "StudentID", nullable = false)
    private Students student;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("subjectId")
    @JoinColumn(name = "SubjectID", nullable = false)
    private Subjects subject;

    @Column(name = "RetakeReason", length = 500)
    private String reason;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "Processed", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean processed = false;  // Thêm cột đánh dấu đã xử lý chưa (rất hữu ích!)

    @Column(name = "Notes", length = 1000)
    private String notes; // Ghi chú thêm nếu cần

    // ==================== Constructors ====================

    public TemporaryRetakeSubjects() {
        this.createdAt = LocalDateTime.now();
    }

    public TemporaryRetakeSubjects(Students student, Subjects subject, String reason) {
        this.id = new RetakeSubjectsId(student.getId(), subject.getSubjectId());
        this.student = student;
        this.subject = subject;
        this.reason = reason;
        this.createdAt = LocalDateTime.now();
        this.processed = false;
    }

    public TemporaryRetakeSubjects(Students student, Subjects subject, String retakeReason, String notes) {
        this(student, subject, retakeReason);
        this.notes = notes;
    }
}