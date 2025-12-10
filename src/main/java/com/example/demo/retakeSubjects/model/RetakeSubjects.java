package com.example.demo.retakeSubjects.model;

import com.example.demo.user.student.model.Students;
import com.example.demo.subject.abstractSubject.model.Subjects;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "RetakeSubjects")
@Getter
@Setter
public class RetakeSubjects {

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
    private String retakeReason;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;

    // NEW: whether this retake subject is allowed to be added to other classes
    @Column(name = "AllowedInOtherClasses", nullable = false)
    private boolean allowedInOtherClasses = false;

    public RetakeSubjects() {
        this.createdAt = LocalDateTime.now();
    }

    public RetakeSubjects(Students student, Subjects subject, String retakeReason) {
        this.id = new RetakeSubjectsId(student.getId(), subject.getSubjectId());
        this.student = student;
        this.subject = subject;
        this.retakeReason = retakeReason;
        this.createdAt = LocalDateTime.now();
    }
}
