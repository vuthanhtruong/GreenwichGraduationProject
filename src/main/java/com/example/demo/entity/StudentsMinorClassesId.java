package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Data
public class StudentsMinorClassesId implements Serializable {

    @Column(name = "StudentID", nullable = false)
    private String studentId;

    @Column(name = "MinorClassID", nullable = false)
    private String minorClassId;

    public StudentsMinorClassesId() {}

    public StudentsMinorClassesId(String studentId, String minorClassId) {
        this.studentId = studentId;
        this.minorClassId = minorClassId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StudentsMinorClassesId)) return false;
        StudentsMinorClassesId that = (StudentsMinorClassesId) o;
        return Objects.equals(studentId, that.studentId) &&
                Objects.equals(minorClassId, that.minorClassId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId, minorClassId);
    }
}