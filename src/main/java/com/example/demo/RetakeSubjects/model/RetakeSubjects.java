package com.example.demo.RetakeSubjects.model;

import com.example.demo.user.student.model.Students;
import com.example.demo.studentRequiredMajorSubjects.model.StudentRetakeSubjectsId;
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
    private StudentRetakeSubjectsId id;

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

    public RetakeSubjects() {
        this.createdAt = LocalDateTime.now();
    }

    public RetakeSubjects(Students student, Subjects subject, String retakeReason) {
        this.id = new StudentRetakeSubjectsId(student.getId(), subject.getSubjectId());
        this.student = student;
        this.subject = subject;
        this.retakeReason = retakeReason;
        this.createdAt = LocalDateTime.now();
    }
}
