package com.example.demo.entity;

import com.example.demo.entity.AbstractClasses.Lecturers_Classes;
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
public class MinorLecturers_MinorClasses extends Lecturers_Classes {

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("lecturerId")
    @JoinColumn(name = "MinorLecturerID")
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