package com.example.demo.entity;
import com.example.demo.entity.AbstractClasses.Subjects;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import java.time.LocalDateTime;
@Entity
@Table(name = "StudentRequiredSubjects")
@Getter
@Setter
public class StudentRequiredSubjects {
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
    @Column(name = "RequiredReason", nullable = true, length = 255)
    private String requiredReason; // Lý do bắt buộc (ví dụ: "Major requirement", "Program mandatory")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AssignedBy", nullable = true)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Staffs assignedBy; // Người gán (Staff hoặc Admin)
    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;
    public StudentRequiredSubjects() {
        this.createdAt = LocalDateTime.now();
    }
    public StudentRequiredSubjects(Students student, Subjects subject, String requiredReason, Staffs assignedBy) {
        this.id = new StudentRequiredSubjectsId(student.getId(), subject.getSubjectId());
        this.student = student;
        this.subject = subject;
        this.requiredReason = requiredReason;
        this.assignedBy = assignedBy;
        this.createdAt = LocalDateTime.now();
    }
}