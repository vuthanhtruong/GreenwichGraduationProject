// com.example.demo.submission.model.SpecializedSubmissionsId.java
package com.example.demo.submission.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Data
public class SpecializedSubmissionsId implements Serializable {

    @Column(name = "SubmittedBy", nullable = false)
    private String submittedBy;

    @Column(name = "SpecializedAssignmentSubmitSlotID", nullable = false)
    private String assignmentSubmitSlotId;

    public SpecializedSubmissionsId() {}

    public SpecializedSubmissionsId(String submittedBy, String assignmentSubmitSlotId) {
        this.submittedBy = submittedBy;
        this.assignmentSubmitSlotId = assignmentSubmitSlotId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SpecializedSubmissionsId)) return false;
        SpecializedSubmissionsId that = (SpecializedSubmissionsId) o;
        return Objects.equals(submittedBy, that.submittedBy) &&
                Objects.equals(assignmentSubmitSlotId, that.assignmentSubmitSlotId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(submittedBy, assignmentSubmitSlotId);
    }
}