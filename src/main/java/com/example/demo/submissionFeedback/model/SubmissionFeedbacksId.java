package com.example.demo.submissionFeedback.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Embeddable
@Data
public class SubmissionFeedbacksId implements Serializable {

    @Column(name = "AnnouncerID", nullable = false)
    private String announcerId;

    @Column(name = "SubmissionID", nullable = false)
    private String submittedBy;

    @Column(name = "AssignmentSubmitSlotID", nullable = false)
    private String assignmentSubmitSlotId;

    public SubmissionFeedbacksId() {}

    public SubmissionFeedbacksId(String announcerId, String submittedBy, String assignmentSubmitSlotId) {
        this.announcerId = announcerId;
        this.submittedBy = submittedBy;
        this.assignmentSubmitSlotId = assignmentSubmitSlotId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubmissionFeedbacksId that = (SubmissionFeedbacksId) o;
        return announcerId.equals(that.announcerId) &&
                submittedBy.equals(that.submittedBy) &&
                assignmentSubmitSlotId.equals(that.assignmentSubmitSlotId);
    }

    @Override
    public int hashCode() {
        int result = announcerId.hashCode();
        result = 31 * result + submittedBy.hashCode();
        result = 31 * result + assignmentSubmitSlotId.hashCode();
        return result;
    }
}