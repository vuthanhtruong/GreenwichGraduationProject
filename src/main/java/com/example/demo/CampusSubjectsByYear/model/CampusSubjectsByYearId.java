package com.example.demo.CampusSubjectsByYear.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Embeddable
@Data
public class CampusSubjectsByYearId implements Serializable {

    @Column(name = "SubjectId")
    private String subjectId;

    @Column(name = "CampusId")
    private String campusId;

    @Column(name = "AdmissionYear")
    private Integer admissionYear;

    // Constructors
    public CampusSubjectsByYearId() {
    }

    public CampusSubjectsByYearId(String subjectId, String campusId, Integer admissionYear) {
        this.subjectId = subjectId;
        this.campusId = campusId;
        this.admissionYear = admissionYear;
    }
}