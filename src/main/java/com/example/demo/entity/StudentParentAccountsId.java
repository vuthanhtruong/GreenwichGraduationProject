package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Data
public class StudentParentAccountsId implements Serializable {

    @Column(name = "StudentID", nullable = false)
    private String studentId;

    @Column(name = "ParentID", nullable = false)
    private String parentId;

    public StudentParentAccountsId() {}

    public StudentParentAccountsId(String studentId, String parentId) {
        this.studentId = studentId;
        this.parentId = parentId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StudentParentAccountsId)) return false;
        StudentParentAccountsId that = (StudentParentAccountsId) o;
        return Objects.equals(studentId, that.studentId) &&
                Objects.equals(parentId, that.parentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId, parentId);
    }
}