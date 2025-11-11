package com.example.demo.timtable.model;

import com.example.demo.classes.minorClasses.model.MinorClasses;
import com.example.demo.user.deputyStaff.model.DeputyStaffs;
import com.example.demo.room.model.Rooms;
import com.example.demo.entity.Enums.DaysOfWeek;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@Entity
@Table(name = "MinorTimetable")
@PrimaryKeyJoinColumn(name = "TimetableID")
@Getter
@Setter
public class MinorTimetable extends Timetable {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MinorClassID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MinorClasses minorClass;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Creator", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private DeputyStaffs creator;

    public MinorTimetable() {}

}