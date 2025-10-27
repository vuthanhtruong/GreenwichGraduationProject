package com.example.demo.students_Classes.abstractStudents_Class.model;

import com.example.demo.classes.abstractClasses.model.Classes;
import com.example.demo.classes.majorClasses.model.MajorClasses;
import com.example.demo.classes.minorClasses.model.MinorClasses;
import com.example.demo.classes.specializedClasses.model.SpecializedClasses;
import com.example.demo.entity.Enums.Notifications;
import com.example.demo.user.student.model.Students;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "Students_Classes")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter

public abstract class Students_Classes {

    @EmbeddedId
    @AttributeOverrides({
            @AttributeOverride(name = "studentId", column = @Column(name = "StudentID", nullable = false)),
            @AttributeOverride(name = "classId",   column = @Column(name = "ClassID",   nullable = false))
    })
    private StudentsClassesId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("studentId")
    @JoinColumn(name = "StudentID")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Students student;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("classId")
    @JoinColumn(name = "ClassID")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Classes classEntity;

    @Enumerated(EnumType.STRING)
    @Column(name = "Notification", nullable = true)
    private Notifications notification;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;

    public String getSubjectName() {
        Classes classEntity = getClassEntity();
        if (classEntity instanceof MajorClasses majorClass) {
            return majorClass.getSubject().getSubjectName();
        } else if (classEntity instanceof MinorClasses minorClass) {
            return minorClass.getMinorSubject().getSubjectName();
        } else if (classEntity instanceof SpecializedClasses specializedClass) {
            return specializedClass.getSpecializedSubject().getSubjectName();
        }
        return "N/A";
    }

    public String getSubjectType() {
        Classes classEntity = getClassEntity();
        if (classEntity instanceof MajorClasses majorClass) {
            return majorClass.getSubject().getSubjectName();
        } else if (classEntity instanceof MinorClasses minorClass) {
            return minorClass.getMinorSubject().getSubjectName();
        } else if (classEntity instanceof SpecializedClasses specializedClass) {
            return specializedClass.getSpecializedSubject().getSubjectName();
        }
        return "N/A";
    }

    public Students_Classes() {}

    public Students_Classes(Students student, Classes classEntity, LocalDateTime createdAt) {
        this.id = new StudentsClassesId(student.getId(), classEntity.getClassId());
        this.student = student;
        this.classEntity = classEntity;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }
}