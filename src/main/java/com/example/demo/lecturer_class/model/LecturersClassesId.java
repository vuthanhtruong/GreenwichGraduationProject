package com.example.demo.lecturer_class.model;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
public class LecturersClassesId implements Serializable {

    private String lecturerId;
    private String classId;

    public LecturersClassesId() {}

    public LecturersClassesId(String lecturerId, String classId) {
        this.lecturerId = lecturerId;
        this.classId = classId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LecturersClassesId that = (LecturersClassesId) o;
        return Objects.equals(lecturerId, that.lecturerId) &&
                Objects.equals(classId, that.classId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lecturerId, classId);
    }
}