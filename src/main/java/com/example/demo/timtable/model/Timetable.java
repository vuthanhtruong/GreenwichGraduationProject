package com.example.demo.timtable.model;

import com.example.demo.entity.Enums.DaysOfWeek;
import com.example.demo.room.model.Rooms;
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
    private DaysOfWeek dayOfWeek;

    @Column(name = "Date", nullable = true)
    private LocalDate date;

    public Timetable() {}

    public Timetable(String timetableId, Rooms room, Slots slot, DaysOfWeek dayOfWeek, LocalDate date) {
        this.timetableId = timetableId;
        this.room = room;
        this.slot = slot;
        this.dayOfWeek = dayOfWeek;
        this.date = date;
    }
}