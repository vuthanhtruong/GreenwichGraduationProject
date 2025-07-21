package com.example.demo.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Embeddable
@Data
public class LecturersClassesId implements Serializable {

    private String lecturerId;
    private String classId;

    // Constructor mặc định (yêu cầu cho JPA)
    public LecturersClassesId() {}

    // Constructor với tham số
    public LecturersClassesId(String lecturerId, String classId) {
        this.lecturerId = lecturerId;
        this.classId = classId;
    }

    // Nếu không dùng Lombok, tự viết equals và hashCode
    /*
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LecturersClassesId)) return false;
        LecturersClassesId that = (LecturersClassesId) o;
        return lecturerId.equals(that.lecturerId) && classId.equals(that.classId);
    }

    @Override
    public int hashCode() {
        return 31 * lecturerId.hashCode() + classId.hashCode();
    }
    */
}