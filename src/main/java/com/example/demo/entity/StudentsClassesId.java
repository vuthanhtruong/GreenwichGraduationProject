package com.example.demo.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Embeddable
@Data
public class StudentsClassesId implements Serializable {

    private String studentId;
    private String classId;

    // Constructor mặc định (yêu cầu cho JPA)
    public StudentsClassesId() {}

    // Constructor với tham số
    public StudentsClassesId(String studentId, String classId) {
        this.studentId = studentId;
        this.classId = classId;
    }

    // Nếu không dùng Lombok, tự viết equals và hashCode
    /*
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StudentsClassesId)) return false;
        StudentsClassesId that = (StudentsClassesId) o;
        return studentId.equals(that.studentId) && classId.equals(that.classId);
    }

    @Override
    public int hashCode() {
        return 31 * studentId.hashCode() + classId.hashCode();
    }
    */
}