package com.example.demo.academicTranscript.model;

import com.example.demo.classes.majorClasses.model.MinorClasses;
import com.example.demo.user.deputyStaff.model.DeputyStaffs;
import com.example.demo.entity.Enums.Grades;
import com.example.demo.user.student.model.Students;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("MINOR")
@Getter
@Setter
public class MinorAcademicTranscripts extends AcademicTranscripts {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ClassID", insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MinorClasses minorClass;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Creator", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private DeputyStaffs creator;

    public MinorAcademicTranscripts() {}

    public MinorAcademicTranscripts(String transcriptId, Students student, MinorClasses minorClass, Grades grade, LocalDateTime createdAt, DeputyStaffs creator) {
        super(transcriptId, student, grade, createdAt);
        this.minorClass = minorClass;
        if (creator == null) {
            throw new IllegalArgumentException("Creator cannot be null");
        }
        this.creator = creator;
    }
}