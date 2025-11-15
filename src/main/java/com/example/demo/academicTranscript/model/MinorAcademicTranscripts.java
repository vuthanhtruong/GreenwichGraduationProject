package com.example.demo.academicTranscript.model;

import com.example.demo.classes.minorClasses.model.MinorClasses;
import com.example.demo.entity.Enums.Grades;
import com.example.demo.entity.Enums.YourNotification;
import com.example.demo.user.deputyStaff.model.DeputyStaffs;
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
    @JoinColumn(name = "ClassID")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MinorClasses minorClass;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Creator", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private DeputyStaffs creator;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", length = 50, nullable = false)
    private YourNotification notificationType;

    @Override
    public String getSubjectName() {
        return minorClass != null && minorClass.getMinorSubject() != null
                ? minorClass.getMinorSubject().getSubjectName()
                : "N/A";
    }

    @Override
    public String getSubjectId() {
        return minorClass != null && minorClass.getMinorSubject() != null
                ? minorClass.getMinorSubject().getSubjectId()
                : "N/A";
    }

    public MinorAcademicTranscripts() {
        this.notificationType = YourNotification.NOTIFICATION_010;
    }

    public MinorAcademicTranscripts(String transcriptId, Students student, MinorClasses minorClass,
                                    Grades grade, LocalDateTime createdAt, DeputyStaffs creator) {
        super(transcriptId, student, grade, createdAt);
        this.minorClass = minorClass;
        if (creator == null) {
            throw new IllegalArgumentException("Creator cannot be null");
        }
        this.creator = creator;
        this.notificationType = YourNotification.NOTIFICATION_010;
    }
}
