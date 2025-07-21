package com.example.demo.entity;

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

    @Id
    @Column(name = "SubmissionID")
    private String submissionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SubmittedBy", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Students submittedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AssignmentSubmitSlotID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private AssignmentSubmitSlots assignmentSubmitSlot;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;
}