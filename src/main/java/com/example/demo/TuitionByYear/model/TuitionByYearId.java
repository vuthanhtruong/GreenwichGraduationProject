package com.example.demo.TuitionByYear.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;
@Embeddable
@Data
public class TuitionByYearId implements Serializable {

    @Column(name = "SubjectID", nullable = false)
    private String subjectId;

    @Column(name = "Admission_Year", nullable = false)
    private Integer admissionYear;

    @Column(name = "CampusID", nullable = false)
    private String campusId;

    public TuitionByYearId() {}

    public TuitionByYearId(String subjectId, Integer admissionYear, String campusId) {
        this.subjectId = subjectId;
        this.admissionYear = admissionYear;
        this.campusId = campusId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TuitionByYearId)) return false; // ✅ fix ở đây
        TuitionByYearId that = (TuitionByYearId) o;
        return Objects.equals(subjectId, that.subjectId) &&
                Objects.equals(admissionYear, that.admissionYear) &&
                Objects.equals(campusId, that.campusId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subjectId, admissionYear, campusId);
    }
}
