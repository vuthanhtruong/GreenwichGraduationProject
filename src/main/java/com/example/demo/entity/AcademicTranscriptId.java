package com.example.demo.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Embeddable
@Data
public class AcademicTranscriptId implements Serializable {

    private String numberId;
    private String subjectId;

    // Constructor mặc định (yêu cầu cho JPA)
    public AcademicTranscriptId() {}

    // Constructor với tham số
    public AcademicTranscriptId(String numberId, String subjectId) {
        this.numberId = numberId;
        this.subjectId = subjectId;
    }

    // Nếu không dùng Lombok, tự viết equals và hashCode
    /*
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AcademicTranscriptId)) return false;
        AcademicTranscriptId that = (AcademicTranscriptId) o;
        return numberId.equals(that.numberId) && subjectId.equals(that.subjectId);
    }

    @Override
    public int hashCode() {
        return 31 * numberId.hashCode() + subjectId.hashCode();
    }
    */
}