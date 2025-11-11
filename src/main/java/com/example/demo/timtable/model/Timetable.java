package com.example.demo.timtable.model;

import com.example.demo.entity.Enums.DaysOfWeek;
import com.example.demo.room.model.Rooms;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

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

    @Column(name = "DayOfTheWeek", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private DaysOfWeek dayOfWeek;

    @Column(name = "WeekOfYear", nullable = false)
    private Integer weekOfYear;  // THAY date → weekOfYear

    @Column(name = "Year", nullable = false)
    private Integer year;  // Năm của tuần (ví dụ: 2025)

    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;

    public Timetable() {}

    public Timetable(String timetableId, Rooms room, Slots slot, DaysOfWeek dayOfWeek, Integer weekOfYear) {
        this.timetableId = timetableId;
        this.room = room;
        this.slot = slot;
        this.dayOfWeek = dayOfWeek;
        this.weekOfYear = weekOfYear;
    }
}