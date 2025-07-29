package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Embeddable
@Data
public class StudentsClassesId implements Serializable {

    @Column(name = "StudentID")
    private String studentId;

    @Column(name = "ClassID")
    private String classId;

    public StudentsClassesId() {}

    public StudentsClassesId(String studentId, String classId) {
        this.studentId = studentId;
        this.classId = classId;
    }
}