package com.example.demo.entity;

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

    public TuitionByYearId() {}

    public TuitionByYearId(String subjectId, Integer admissionYear) {
        this.subjectId = subjectId;
        this.admissionYear = admissionYear;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TuitionByYearId)) return false;
        TuitionByYearId that = (TuitionByYearId) o;
        return Objects.equals(subjectId, that.subjectId) &&
                Objects.equals(admissionYear, that.admissionYear);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subjectId, admissionYear);
    }
}