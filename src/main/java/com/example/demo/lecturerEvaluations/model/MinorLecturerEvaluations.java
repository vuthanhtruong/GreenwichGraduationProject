// src/main/java/com/example/demo/entity/MinorLecturerEvaluations.java
package com.example.demo.lecturerEvaluations.model;

import com.example.demo.classes.minorClasses.model.MinorClasses;
import com.example.demo.user.minorLecturer.model.MinorLecturers;
import com.example.demo.user.student.model.Students;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "MinorLecturerEvaluations")
@PrimaryKeyJoinColumn(name = "EvaluationID")
@DiscriminatorValue("MINOR")
@Getter
@Setter
public class MinorLecturerEvaluations extends LecturerEvaluations {
    @Override
    public String getLecturerId() {
        return lecturer.getId();
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MinorLecturerID", nullable = false)
    private MinorLecturers lecturer;

    public MinorLecturerEvaluations() {
        super();
    }

    public MinorLecturerEvaluations(String evaluationId, Students reviewer, MinorLecturers lecturer, MinorClasses classEntity, String text) {
        super(evaluationId, reviewer, classEntity, text);
        this.lecturer = lecturer;
    }
}