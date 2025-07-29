package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "Lecturers_Classes")
@Getter
@Setter
public class Lecturers_Classes {

    @EmbeddedId
    private LecturersClassesId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("lecturerId")
    @JoinColumn(name = "LecturerID")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Lecturers lecturer;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("classId")
    @JoinColumn(name = "ClassID")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Classes classEntity;

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
}