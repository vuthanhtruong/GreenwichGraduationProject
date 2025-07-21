package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@Entity
@Table(name = "Timetable")
@Getter
@Setter
public class Timetable {

    @Id
    @Column(name = "TimetableID")
    private String timetableId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RoomID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Rooms room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ClassID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Classes classEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SlotID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Slots slot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Creator", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Staffs creator;

    @Column(name = "DayOfTheWeek", nullable = true, length = 20)
    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfTheWeek;

    @Column(name = "Date", nullable = true)
    private LocalDate date;
}