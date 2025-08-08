package com.example.demo.entity;

import com.example.demo.entity.AbstractClasses.AcademicTranscripts;
import com.example.demo.entity.Enums.Grades;
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