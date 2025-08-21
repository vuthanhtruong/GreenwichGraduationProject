package com.example.demo.entity;

import com.example.demo.student.model.Students;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "Submissions")
@Getter
@Setter
public class Submissions {

    @EmbeddedId
    private SubmissionsId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("submittedBy")
    @JoinColumn(name = "SubmittedBy", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Students submittedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("assignmentSubmitSlotId")
    @JoinColumn(name = "AssignmentSubmitSlotID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private AssignmentSubmitSlots assignmentSubmitSlot;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;

    public Submissions() {}

    public Submissions(Students submittedBy, AssignmentSubmitSlots assignmentSubmitSlot, LocalDateTime createdAt) {
        this.id = new SubmissionsId(submittedBy.getId(), assignmentSubmitSlot.getPostId());
        this.submittedBy = submittedBy;
        this.assignmentSubmitSlot = assignmentSubmitSlot;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }
}