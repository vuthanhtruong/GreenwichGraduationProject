package com.example.demo.document.model;

import com.example.demo.submission.model.Submissions;
import com.example.demo.user.student.model.Students;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.security.SecureRandom;

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
    @Column(name = "FileData", nullable = true,columnDefinition = "LONGBLOB")
    private byte[] fileData;

    // TỰ ĐỘNG GÁN ID TRƯỚC KHI LƯU
    @PrePersist
    public void generateId() {
        if (this.submissionDocumentId == null) {
            this.submissionDocumentId = generateUniqueId();
        }
    }

    private String generateUniqueId() {
        String prefix = (submission != null && submission.getSubmittedBy() != null)
                ? submission.getSubmittedBy().getId() : "STU";
        String slotId = (submission != null && submission.getAssignmentSubmitSlot() != null)
                ? submission.getAssignmentSubmitSlot().getPostId() : "SLOT";

        SecureRandom random = new SecureRandom();
        String documentId;
        do {
            String randomPart = String.format("%04d", random.nextInt(10000));
            documentId = prefix + "-" + slotId + "-DOC" + randomPart;
        } while (false); // Có thể thêm check DB nếu cần, nhưng tạm bỏ để đơn giản
        return documentId;
    }
}