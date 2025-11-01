package com.example.demo.document.model;

import com.example.demo.submission.model.SpecializedSubmissions;
import com.example.demo.user.student.model.Students;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "SpecializedSubmissionDocuments")
@Getter
@Setter
public class SpecializedSubmissionDocuments {

    @Id
    @Column(name = "SpecializedSubmissionDocumentID")
    private String submissionDocumentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "SubmissionID", referencedColumnName = "SubmittedBy"),
            @JoinColumn(name = "SpecializedAssignmentSubmitSlotID", referencedColumnName = "SpecializedAssignmentSubmitSlotID")
    })
    @OnDelete(action = OnDeleteAction.CASCADE)
    private SpecializedSubmissions submission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Creator")
    private Students creator;

    @Column(name = "FilePath", length = 500)
    private String filePath;

    @Lob
    @Column(name = "FileData", nullable = true,columnDefinition = "LONGBLOB")
    private byte[] fileData;

    @PrePersist
    public void generateId() {
        if (this.submissionDocumentId == null) {
            this.submissionDocumentId = java.util.UUID.randomUUID().toString();
        }
    }
}