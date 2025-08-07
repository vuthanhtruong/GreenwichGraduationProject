package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "Students_MinorClasses")
@Getter
@Setter
public class Students_MinorClasses {

    @EmbeddedId
    private StudentsMinorClassesId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("studentId")
    @JoinColumn(name = "StudentID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Students student;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("minorClassId")
    @JoinColumn(name = "MinorClassID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MinorClasses minorClass;

    @Enumerated(EnumType.STRING)
    @Column(name = "Notification", nullable = true)
    private Notifications notification;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AddedBy", nullable = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private DeputyStaffs addedBy;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;

    public Students_MinorClasses() {}

    public Students_MinorClasses(Students student, MinorClasses minorClass, DeputyStaffs addedBy, LocalDateTime createdAt) {
        this.id = new StudentsMinorClassesId(student.getId(), minorClass.getMinorClassId());
        this.student = student;
        this.minorClass = minorClass;
        this.addedBy = addedBy;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }
}