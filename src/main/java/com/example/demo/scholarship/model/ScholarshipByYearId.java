package com.example.demo.scholarship.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
public class ScholarshipByYearId implements Serializable {

    @Column(name = "ScholarshipID", nullable = false)
    private String scholarshipId;

    @Column(name = "AdmissionYear", nullable = false)
    private Integer admissionYear;

    public ScholarshipByYearId() {}

    public ScholarshipByYearId(String scholarshipId, Integer admissionYear) {
        this.scholarshipId = scholarshipId;
        this.admissionYear = admissionYear;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ScholarshipByYearId)) return false;
        ScholarshipByYearId that = (ScholarshipByYearId) o;
        return Objects.equals(scholarshipId, that.scholarshipId) &&
                Objects.equals(admissionYear, that.admissionYear);
    }

    @Override
    public int hashCode() {
        return Objects.hash(scholarshipId, admissionYear);
    }
}