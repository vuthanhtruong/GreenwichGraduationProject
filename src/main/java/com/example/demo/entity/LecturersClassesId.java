package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Data
public class LecturersClassesId implements Serializable {

    @Column(name = "LecturerID")
    private String lecturerId;

    @Column(name = "ClassID")
    private String classId;

    // Default constructor (required by JPA)
    public LecturersClassesId() {}

    // Parameterized constructor
    public LecturersClassesId(String lecturerId, String classId) {
        this.lecturerId = lecturerId;
        this.classId = classId;
    }

    // equals and hashCode are handled by Lombok's @Data, but included explicitly for clarity
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LecturersClassesId)) return false;
        LecturersClassesId that = (LecturersClassesId) o;
        return Objects.equals(lecturerId, that.lecturerId) &&
                Objects.equals(classId, that.classId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lecturerId, classId);
    }
}