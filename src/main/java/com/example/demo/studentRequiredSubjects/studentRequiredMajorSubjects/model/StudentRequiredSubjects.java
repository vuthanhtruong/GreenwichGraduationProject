package com.example.demo.studentRequiredSubjects.studentRequiredMajorSubjects.model;

import com.example.demo.user.student.model.Students;

import com.example.demo.subject.abstractSubject.model.Subjects;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "StudentRequiredSubjects")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
public abstract class StudentRequiredSubjects {

    @EmbeddedId
    private StudentRequiredSubjectsId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("studentId")
    @JoinColumn(name = "StudentID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Students student;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("subjectId")
    @JoinColumn(name = "SubjectID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Subjects subject;

    @Column(name = "RequiredReason", length = 255)
    private String requiredReason;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;

    public StudentRequiredSubjects() {
        this.createdAt = LocalDateTime.now();
    }

    public StudentRequiredSubjects(Students student, Subjects subject, String requiredReason) {
        this.id = new StudentRequiredSubjectsId(student.getId(), subject.getSubjectId());
        this.student = student;
        this.subject = subject;
        this.requiredReason = requiredReason;
        this.createdAt = LocalDateTime.now();
    }
}
