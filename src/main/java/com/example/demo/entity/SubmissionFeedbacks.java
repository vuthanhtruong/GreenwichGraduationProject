package com.example.demo.entity;

import com.example.demo.entity.Enums.Grades;
import com.example.demo.user.majorLecturer.model.MajorLecturers;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "SubmissionFeedbacks")
@Getter
@Setter
public class SubmissionFeedbacks {

    @EmbeddedId
    private SubmissionFeedbacksId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("announcerId")
    @JoinColumn(name = "AnnouncerID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MajorLecturers announcer;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("submissionId")
    @JoinColumns({
            @JoinColumn(name = "SubmissionID", referencedColumnName = "SubmittedBy", nullable = false),
            @JoinColumn(name = "AssignmentSubmitSlotID", referencedColumnName = "AssignmentSubmitSlotID", nullable = false)
    })
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Submissions submission;

    @Column(name = "Content", nullable = true, length = 1000)
    private String content;

    @Column(name = "Grade", nullable = true)
    @Enumerated(EnumType.STRING)
    private Grades grade;

    public SubmissionFeedbacks() {}

    public SubmissionFeedbacks(MajorLecturers announcer, Submissions submission) {
        if (announcer == null || submission == null) {
            throw new IllegalArgumentException("Announcer and submission cannot be null");
        }
        this.id = new SubmissionFeedbacksId(announcer.getId(), submission.getId().getSubmittedBy(), submission.getId().getAssignmentSubmitSlotId());
        this.announcer = announcer;
        this.submission = submission;
    }
}