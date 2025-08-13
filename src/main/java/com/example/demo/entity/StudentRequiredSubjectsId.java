package com.example.demo.entity;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.Objects;
@Embeddable
@Data
@NoArgsConstructor
public class StudentRequiredSubjectsId implements Serializable {
    @Column(name = "StudentID", nullable = false)
    private String studentId;
    @Column(name = "SubjectID", nullable = false)
    private String subjectId;
    public StudentRequiredSubjectsId(String studentId, String subjectId) {
        this.studentId = studentId;
        this.subjectId = subjectId;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudentRequiredSubjectsId that = (StudentRequiredSubjectsId) o;
        return Objects.equals(studentId, that.studentId) && Objects.equals(subjectId, that.subjectId);
    }
    @Override
    public int hashCode() {
        return Objects.hash(studentId, subjectId);
    }
}