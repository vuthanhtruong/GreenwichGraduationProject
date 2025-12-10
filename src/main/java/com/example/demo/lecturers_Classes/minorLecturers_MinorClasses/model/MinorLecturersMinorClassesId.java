package com.example.demo.lecturers_Classes.minorLecturers_MinorClasses.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Data
public class MinorLecturersMinorClassesId implements Serializable {

    @Column(name = "MinorLecturerID", nullable = false)
    private String minorLecturerId;

    @Column(name = "MinorClassID", nullable = false)
    private String minorClassId;

    public MinorLecturersMinorClassesId() {}

    public MinorLecturersMinorClassesId(String minorLecturerId, String minorClassId) {
        this.minorLecturerId = minorLecturerId;
        this.minorClassId = minorClassId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MinorLecturersMinorClassesId)) return false;
        MinorLecturersMinorClassesId that = (MinorLecturersMinorClassesId) o;
        return Objects.equals(minorLecturerId, that.minorLecturerId) &&
                Objects.equals(minorClassId, that.minorClassId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(minorLecturerId, minorClassId);
    }
}