package com.example.demo.lecturers_Classes.majorLecturers_Specializations.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Embeddable
@Getter
@Setter
public class MajorLecturersSpecializationsId {
    @Column(name = "LecturerID", nullable = false)
    private String lecturerId;

    @Column(name = "SpecializationID", nullable = false)
    private String specializationId;

    public MajorLecturersSpecializationsId() {}

    public MajorLecturersSpecializationsId(String lecturerId, String specializationId) {
        this.lecturerId = lecturerId;
        this.specializationId = specializationId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MajorLecturersSpecializationsId)) return false;
        MajorLecturersSpecializationsId that = (MajorLecturersSpecializationsId) o;
        return Objects.equals(lecturerId, that.lecturerId) &&
                Objects.equals(specializationId, that.specializationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lecturerId, specializationId);
    }
}
