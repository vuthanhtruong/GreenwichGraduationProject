package com.example.demo.entity;

import com.example.demo.specializedClasses.model.SpecializedClasses;
import com.example.demo.entity.AbstractClasses.Timetable;
import com.example.demo.entity.Enums.DaysOfWeek;
import com.example.demo.room.model.Rooms;
import com.example.demo.staff.model.Staffs;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@Entity
@Table(name = "SpecializedTimetable")
@PrimaryKeyJoinColumn(name = "TimetableID")
@Getter
@Setter
public class SpecializedTimetable extends Timetable {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ClassID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private SpecializedClasses specializedClass;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Creator", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Staffs creator;

    public SpecializedTimetable() {}

    public SpecializedTimetable(String timetableId, Rooms room, Slots slot, DaysOfWeek dayOfTheWeek, LocalDate date, SpecializedClasses specializedClass, Staffs creator) {
        super(timetableId, room, slot, dayOfTheWeek, date);
        this.specializedClass = specializedClass;
        this.creator = creator;
    }
}
