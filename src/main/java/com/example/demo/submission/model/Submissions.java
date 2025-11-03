// com.example.demo.submission.model.Submissions.java
package com.example.demo.submission.model;

import com.example.demo.document.model.SubmissionDocuments;
import com.example.demo.post.majorAssignmentSubmitSlots.model.AssignmentSubmitSlots;
import com.example.demo.submissionFeedback.model.SubmissionFeedbacks;
import com.example.demo.user.student.model.Students;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Submissions")
@Getter
@Setter
public class Submissions {

    @EmbeddedId
    private SubmissionsId id;

    // === Quan hệ với Student ===
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("submittedBy")
    @JoinColumn(name = "SubmittedBy", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Students submittedBy;

    // === Quan hệ với AssignmentSubmitSlot ===
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("assignmentSubmitSlotId")
    @JoinColumn(name = "AssignmentSubmitSlotID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private AssignmentSubmitSlots assignmentSubmitSlot;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;

    // === Quan hệ với Feedback (1-1, optional) ===
    @OneToOne(mappedBy = "submission", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private SubmissionFeedbacks feedback;

    // === Quan hệ với Documents (1-N) ===
    @OneToMany(mappedBy = "submission", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<SubmissionDocuments> submissionDocuments = new ArrayList<>();

    // === Constructors ===
    public Submissions() {}

    public Submissions(Students submittedBy, AssignmentSubmitSlots assignmentSubmitSlot) {
        this(submittedBy, assignmentSubmitSlot, LocalDateTime.now());
    }

    public Submissions(Students submittedBy, AssignmentSubmitSlots assignmentSubmitSlot, LocalDateTime createdAt) {
        this.id = new SubmissionsId(submittedBy.getId(), assignmentSubmitSlot.getPostId());
        this.submittedBy = submittedBy;
        this.assignmentSubmitSlot = assignmentSubmitSlot;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.submissionDocuments = new ArrayList<>();

    }
}