package com.example.demo.AcademicTranscript.model;

import com.example.demo.classes.model.MajorClasses;
import com.example.demo.entity.Enums.Grades;
import com.example.demo.lecturer.model.MajorLecturers;
import com.example.demo.staff.model.Staffs;
import com.example.demo.student.model.Students;
import com.example.demo.majorSubject.model.MajorSubjects;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("MAJOR")
@Getter
@Setter
public class MajorAcademicTranscripts extends AcademicTranscripts {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ClassID", insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MajorClasses majorClass;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Marker", nullable = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MajorLecturers marker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Creator", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Staffs creator;

    public MajorAcademicTranscripts() {}

    public MajorAcademicTranscripts(String transcriptId, Students student, MajorClasses majorClass, Grades grade, LocalDateTime createdAt, Staffs creator) {
        super(transcriptId, student, grade, createdAt);
        this.majorClass = majorClass;
        if (creator == null) {
            throw new IllegalArgumentException("Creator cannot be null");
        }
        this.creator = creator;
    }
}