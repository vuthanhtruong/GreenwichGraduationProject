package com.example.demo.academicTranscript.model;

import com.example.demo.classes.specializedClasses.model.SpecializedClasses;
import com.example.demo.entity.Enums.Grades;
import com.example.demo.user.staff.model.Staffs;
import com.example.demo.user.student.model.Students;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("SPECIALIZED")
@Getter
@Setter
public class SpecializedAcademicTranscripts extends AcademicTranscripts {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ClassID", insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private SpecializedClasses specializedClass;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Creator", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Staffs creator;

    public SpecializedAcademicTranscripts() {}

    public SpecializedAcademicTranscripts(String transcriptId, Students student, SpecializedClasses specializedClass, Grades grade,
                                          LocalDateTime createdAt, Staffs creator) {
        super(transcriptId, student, grade, createdAt);
        this.specializedClass = specializedClass;
        if (creator == null) {
            throw new IllegalArgumentException("Creator cannot be null");
        }
        this.creator = creator;
    }
}