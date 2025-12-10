package com.example.demo.retakeSubjects.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
public class RetakeSubjectsId implements Serializable {

    @Column(name = "StudentID", nullable = false)
    private String studentId;

    @Column(name = "SubjectID", nullable = false)
    private String subjectId;

    public RetakeSubjectsId() {}

    public RetakeSubjectsId(String studentId, String subjectId) {
        this.studentId = studentId;
        this.subjectId = subjectId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RetakeSubjectsId)) return false;
        RetakeSubjectsId that = (RetakeSubjectsId) o;
        return Objects.equals(studentId, that.studentId) &&
                Objects.equals(subjectId, that.subjectId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId, subjectId);
    }
}