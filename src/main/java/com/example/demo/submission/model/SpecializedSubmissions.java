package com.example.demo.submission.model;

import com.example.demo.document.model.SpecializedSubmissionDocuments;
import com.example.demo.post.specializedAssignmentSubmitSlots.model.SpecializedAssignmentSubmitSlots;
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
@Table(name = "SpecializedSubmissions")
@Getter
@Setter
public class SpecializedSubmissions {

    @EmbeddedId
    private SpecializedSubmissionsId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("submittedBy")
    @JoinColumn(name = "SubmittedBy", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Students submittedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("assignmentSubmitSlotId")
    @JoinColumn(name = "SpecializedAssignmentSubmitSlotID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private SpecializedAssignmentSubmitSlots assignmentSubmitSlot;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "submission", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<SpecializedSubmissionDocuments> submissionDocuments = new ArrayList<>();

    public SpecializedSubmissions() {}

    public SpecializedSubmissions(Students submittedBy, SpecializedAssignmentSubmitSlots slot, LocalDateTime createdAt) {
        this.id = new SpecializedSubmissionsId(submittedBy.getId(), slot.getPostId());
        this.submittedBy = submittedBy;
        this.assignmentSubmitSlot = slot;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    public void addDocument(SpecializedSubmissionDocuments doc) {
        submissionDocuments.add(doc);
        doc.setSubmission(this);
    }
}