package com.example.demo.entity;

import com.example.demo.classes.model.MajorClasses;
import com.example.demo.majorStaff.model.Staffs;
import com.example.demo.room.model.Rooms;
import com.example.demo.entity.AbstractClasses.Timetable;
import com.example.demo.entity.Enums.DaysOfWeek;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@Entity
@Table(name = "MajorTimetable")
@PrimaryKeyJoinColumn(name = "TimetableID")
@Getter
@Setter
public class MajorTimetable extends Timetable {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ClassID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MajorClasses classEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Creator", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Staffs creator;

    public MajorTimetable() {}

    public MajorTimetable(String timetableId, Rooms room, Slots slot, DaysOfWeek dayOfTheWeek, LocalDate date, MajorClasses classEntity, Staffs creator) {
        super(timetableId, room, slot, dayOfTheWeek, date);
        this.classEntity = classEntity;
        this.creator = creator;
    }
}