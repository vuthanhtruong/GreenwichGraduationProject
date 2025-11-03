// com.example.demo.submissionFeedback.model.SpecializedSubmissionFeedbacksId.java
package com.example.demo.submissionFeedback.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Data
public class SpecializedSubmissionFeedbacksId implements Serializable {

    @Column(name = "AnnouncerID", nullable = false)
    private String announcerId;

    @Column(name = "SubmittedBy", nullable = false)
    private String submittedBy;

    @Column(name = "SpecializedAssignmentSubmitSlotID", nullable = false)
    private String assignmentSubmitSlotId;

    public SpecializedSubmissionFeedbacksId() {}

    public SpecializedSubmissionFeedbacksId(String announcerId, String submittedBy, String assignmentSubmitSlotId) {
        this.announcerId = announcerId;
        this.submittedBy = submittedBy;
        this.assignmentSubmitSlotId = assignmentSubmitSlotId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SpecializedSubmissionFeedbacksId)) return false;
        SpecializedSubmissionFeedbacksId that = (SpecializedSubmissionFeedbacksId) o;
        return Objects.equals(announcerId, that.announcerId) &&
                Objects.equals(submittedBy, that.submittedBy) &&
                Objects.equals(assignmentSubmitSlotId, that.assignmentSubmitSlotId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(announcerId, submittedBy, assignmentSubmitSlotId);
    }
}