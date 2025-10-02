package com.example.demo.studentRequiredSubjects.model;

import com.example.demo.student.model.Students;
import com.example.demo.subject.model.Subjects;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
public class StudentRetakeSubjectsId implements Serializable {

    @Column(name = "StudentID", nullable = false)
    private String studentId;

    @Column(name = "SubjectID", nullable = false)
    private String subjectId;

    public StudentRetakeSubjectsId() {}

    public StudentRetakeSubjectsId(String studentId, String subjectId) {
        this.studentId = studentId;
        this.subjectId = subjectId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StudentRetakeSubjectsId)) return false;
        StudentRetakeSubjectsId that = (StudentRetakeSubjectsId) o;
        return Objects.equals(studentId, that.studentId) &&
                Objects.equals(subjectId, that.subjectId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId, subjectId);
    }
}