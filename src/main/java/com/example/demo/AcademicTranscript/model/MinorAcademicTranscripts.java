package com.example.demo.AcademicTranscript.model;

import com.example.demo.deputyStaff.model.DeputyStaffs;
import com.example.demo.entity.Enums.Grades;
import com.example.demo.lecturer.model.MinorLecturers;
import com.example.demo.student.model.Students;
import com.example.demo.subject.model.MinorSubjects;
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
    @JoinColumn(name = "SubjectID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MinorSubjects subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Marker", nullable = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MinorLecturers marker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Creator", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private DeputyStaffs creator;

    public MinorAcademicTranscripts() {}

    public MinorAcademicTranscripts(String transcriptId, Students student, MinorSubjects subject, Grades grade, LocalDateTime createdAt, DeputyStaffs creator) {
        super(transcriptId, student, grade, createdAt);
        this.subject = subject;
        if (creator == null) {
            throw new IllegalArgumentException("Creator cannot be null");
        }
        this.creator = creator;
    }
}