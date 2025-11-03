// com.example.demo.submissionFeedback.model.SpecializedSubmissionFeedbacks.java
package com.example.demo.submissionFeedback.model;

import com.example.demo.entity.Enums.Grades;
import com.example.demo.submission.model.SpecializedSubmissions;
import com.example.demo.user.majorLecturer.model.MajorLecturers;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "SpecializedSubmissionFeedbacks")
@Getter
@Setter
public class SpecializedSubmissionFeedbacks {

    @EmbeddedId
    private SpecializedSubmissionFeedbacksId id;

    // === Người chấm (giảng viên) ===
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("announcerId")
    @JoinColumn(name = "AnnouncerID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MajorLecturers announcer;

    // === Liên kết đến Submission (composite key) ===
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "SubmittedBy",
                    referencedColumnName = "SubmittedBy",
                    nullable = false, insertable = false, updatable = false),
            @JoinColumn(name = "SpecializedAssignmentSubmitSlotID",
                    referencedColumnName = "SpecializedAssignmentSubmitSlotID",
                    nullable = false, insertable = false, updatable = false)
    })
    @OnDelete(action = OnDeleteAction.CASCADE)
    private SpecializedSubmissions submission;

    @Column(name = "Content", nullable = true, length = 1000)
    private String content;

    @Column(name = "Grade", nullable = true)
    @Enumerated(EnumType.STRING)
    private Grades grade;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // === Constructors ===
    public SpecializedSubmissionFeedbacks() {}

    public SpecializedSubmissionFeedbacks(MajorLecturers announcer, SpecializedSubmissions submission) {
        if (announcer == null || submission == null) {
            throw new IllegalArgumentException("Announcer and submission cannot be null");
        }
        this.id = new SpecializedSubmissionFeedbacksId(
                announcer.getId(),
                submission.getId().getSubmittedBy(),
                submission.getId().getAssignmentSubmitSlotId()
        );
        this.announcer = announcer;
        this.submission = submission;
        this.createdAt = LocalDateTime.now();
    }
}