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
@DiscriminatorValue("MAJOR")
@Getter
@Setter
public class MajorAcademicTranscripts extends AcademicTranscripts {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SubjectID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MajorSubjects subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Marker", nullable = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MajorLecturers marker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Creator", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Staffs creator;

    public MajorAcademicTranscripts() {}

    public MajorAcademicTranscripts(String transcriptId, Students student, MajorSubjects subject, Grades grade, LocalDateTime createdAt, Staffs creator) {
        super(transcriptId, student, grade, createdAt);
        this.subject = subject;
        if (creator == null) {
            throw new IllegalArgumentException("Creator cannot be null");
        }
        this.creator = creator;
    }
}