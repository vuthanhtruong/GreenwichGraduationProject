package com.example.demo.entity;

import com.example.demo.user.student.model.Students;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "SubmissionDocuments")
@Getter
@Setter
public class SubmissionDocuments {

    @Id
    @Column(name = "SubmissionDocumentID")
    private String submissionDocumentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "SubmissionID", referencedColumnName = "SubmittedBy", nullable = false),
            @JoinColumn(name = "AssignmentSubmitSlotID", referencedColumnName = "AssignmentSubmitSlotID", nullable = false)
    })
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Submissions submission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Creator", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Students creator;

    @Column(name = "FilePath", nullable = true, length = 500)
    private String filePath;

    @Lob
    @Column(name = "FileData", nullable = true)
    private byte[] fileData;
}