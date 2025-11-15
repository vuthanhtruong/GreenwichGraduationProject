package com.example.demo.lecturers_Classes.minorLecturers_MinorClasses.model;

import com.example.demo.classes.minorClasses.model.MinorClasses;
import com.example.demo.lecturers_Classes.abstractLecturers_Classes.model.Lecturers_Classes;
import com.example.demo.user.deputyStaff.model.DeputyStaffs;
import com.example.demo.user.minorLecturer.model.MinorLecturers;
import com.example.demo.entity.Enums.YourNotification;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "MinorLecturers_MinorClasses")
@Getter
@Setter
@PrimaryKeyJoinColumns({
        @PrimaryKeyJoinColumn(name = "LecturerID", referencedColumnName = "LecturerID"),
        @PrimaryKeyJoinColumn(name = "ClassID", referencedColumnName = "ClassID")
})
public class MinorLecturers_MinorClasses extends Lecturers_Classes {

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("lecturerId")
    @JoinColumn(name = "LecturerID")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MinorLecturers lecturer;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("classId")
    @JoinColumn(name = "ClassID")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MinorClasses minorClass;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AddedBy")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private DeputyStaffs addedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", length = 50)
    private YourNotification notificationType;

    public MinorLecturers_MinorClasses() {
        this.notificationType = YourNotification.NOTIFICATION_003;
    }

    public MinorLecturers_MinorClasses(MinorLecturers lecturer, MinorClasses minorClass, LocalDateTime createdAt, DeputyStaffs addedBy) {
        super(lecturer.getId(), minorClass.getClassId(), createdAt);
        this.lecturer = lecturer;
        this.minorClass = minorClass;
        this.addedBy = addedBy;
        this.notificationType = YourNotification.NOTIFICATION_003;
    }

    @Override
    public String getSession() {
        return minorClass != null ? String.valueOf(minorClass.getSession()) : null;
    }

    @Override
    public Integer getSlotQuantity() {
        return minorClass != null ? minorClass.getSlotQuantity() : null;
    }

    @Override public String getLecturerId() { return lecturer != null ? lecturer.getId() : null; }
    @Override public String getLecturerName() { return lecturer != null ? lecturer.getFullName() : null; }
    @Override public Object getLecturerEntity() { return lecturer; }
    @Override public String getClassId() { return minorClass != null ? minorClass.getClassId() : null; }
    @Override public String getClassName() { return minorClass != null ? minorClass.getNameClass() : null; }
    @Override public String getSubjectName() { return minorClass != null && minorClass.getMinorSubject() != null ? minorClass.getMinorSubject().getSubjectName() : null; }
    @Override public String getSubjectCode() { return minorClass != null && minorClass.getMinorSubject() != null ? minorClass.getMinorSubject().getSubjectId() : null; }
}