package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@Entity
@Table(name = "Timetable")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
public abstract class Timetable {

    @Id
    @Column(name = "TimetableID")
    private String timetableId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RoomID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Rooms room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SlotID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Slots slot;

    @Column(name = "DayOfTheWeek", nullable = true, length = 20)
    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfTheWeek;

    @Column(name = "Date", nullable = true)
    private LocalDate date;

    public Timetable() {}

    public Timetable(String timetableId, Rooms room, Slots slot, DayOfWeek dayOfTheWeek, LocalDate date) {
        this.timetableId = timetableId;
        this.room = room;
        this.slot = slot;
        this.dayOfTheWeek = dayOfTheWeek;
        this.date = date;
    }
}