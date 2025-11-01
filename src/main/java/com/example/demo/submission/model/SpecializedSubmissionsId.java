package com.example.demo.submission.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Embeddable
@Data
public class SpecializedSubmissionsId implements Serializable {
    @Column(name = "SubmittedBy")
    private String submittedBy;

    @Column(name = "SpecializedAssignmentSubmitSlotID")
    private String assignmentSubmitSlotId;

    public SpecializedSubmissionsId() {}
    public SpecializedSubmissionsId(String submittedBy, String slotId) {
        this.submittedBy = submittedBy;
        this.assignmentSubmitSlotId = slotId;
    }
}