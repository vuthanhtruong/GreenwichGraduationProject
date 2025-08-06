package com.example.demo.entity;

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
public class MinorLecturers_MinorClasses {

    @EmbeddedId
    private MinorLecturersMinorClassesId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("minorLecturerId")
    @JoinColumn(name = "MinorLecturerID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MinorLecturers minorLecturer;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("minorClassId")
    @JoinColumn(name = "MinorClassID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MinorClasses minorClass;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NotificationID", nullable = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Notifications notification;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AddedBy", nullable = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Staffs addedBy;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;

    public MinorLecturers_MinorClasses() {}

    public MinorLecturers_MinorClasses(MinorLecturers minorLecturer, MinorClasses minorClass, Staffs addedBy, LocalDateTime createdAt) {
        this.id = new MinorLecturersMinorClassesId(minorLecturer.getId(), minorClass.getMinorClassId());
        this.minorLecturer = minorLecturer;
        this.minorClass = minorClass;
        this.addedBy = addedBy;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }
}