package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Embeddable
@Data
public class SubmissionsId implements Serializable {

    @Column(name = "SubmittedBy", nullable = false)
    private String submittedBy;

    @Column(name = "AssignmentSubmitSlotID", nullable = false)
    private String assignmentSubmitSlotId;

    public SubmissionsId() {}

    public SubmissionsId(String submittedBy, String assignmentSubmitSlotId) {
        this.submittedBy = submittedBy;
        this.assignmentSubmitSlotId = assignmentSubmitSlotId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubmissionsId that = (SubmissionsId) o;
        return submittedBy.equals(that.submittedBy) && assignmentSubmitSlotId.equals(that.assignmentSubmitSlotId);
    }

    @Override
    public int hashCode() {
        return 31 * submittedBy.hashCode() + assignmentSubmitSlotId.hashCode();
    }
}