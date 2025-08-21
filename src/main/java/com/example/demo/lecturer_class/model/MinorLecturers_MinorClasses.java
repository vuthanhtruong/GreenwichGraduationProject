package com.example.demo.lecturer_class.model;

import com.example.demo.classes.model.MinorClasses;
import com.example.demo.entity.DeputyStaffs;
import com.example.demo.lecturer.model.MinorLecturers;
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
        @PrimaryKeyJoinColumn(name = "ClassID",    referencedColumnName = "ClassID")
})
public class MinorLecturers_MinorClasses extends Lecturers_Classes {

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("lecturerId")
    @JoinColumn(name = "LecturerID")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MinorLecturers minorLecturer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ClassID", insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MinorClasses minorClass;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AddedBy")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private DeputyStaffs addedBy;

    public MinorLecturers_MinorClasses() {}

    public MinorLecturers_MinorClasses(MinorLecturers minorLecturer, MinorClasses minorClass, LocalDateTime createdAt, DeputyStaffs addedBy) {
        super(minorLecturer.getId(), minorClass, createdAt);
        this.minorLecturer = minorLecturer;
        this.minorClass = minorClass;
        this.addedBy = addedBy;
    }
}