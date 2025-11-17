// src/main/java/com/example/demo/entity/MajorLecturerEvaluations.java
package com.example.demo.lecturerEvaluations.model;

import com.example.demo.classes.majorClasses.model.MajorClasses;
import com.example.demo.user.majorLecturer.model.MajorLecturers;
import com.example.demo.user.student.model.Students;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "MajorLecturerEvaluations")
@PrimaryKeyJoinColumn(name = "EvaluationID")
@DiscriminatorValue("MAJOR")
@Getter
@Setter
public class MajorLecturerEvaluations extends LecturerEvaluations {
    @Override
    public String getLecturerId() {
        return lecturer.getId();
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LecturerID", nullable = false)
    private MajorLecturers lecturer;

    public MajorLecturerEvaluations() {
        super();
    }

    public MajorLecturerEvaluations(String evaluationId, Students reviewer, MajorLecturers lecturer, MajorClasses classEntity, String text) {
        super(evaluationId, reviewer, classEntity, text);
        this.lecturer = lecturer;
    }
}