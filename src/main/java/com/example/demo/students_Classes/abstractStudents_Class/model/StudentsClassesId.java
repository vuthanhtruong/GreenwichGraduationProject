package com.example.demo.students_Classes.abstractStudents_Class.model;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
public class StudentsClassesId implements Serializable {

    private String studentId;
    private String classId;

    public StudentsClassesId() {}

    public StudentsClassesId(String studentId, String classId) {
        this.studentId = studentId;
        this.classId = classId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudentsClassesId that = (StudentsClassesId) o;
        return Objects.equals(studentId, that.studentId) &&
                Objects.equals(classId, that.classId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId, classId);
    }
}