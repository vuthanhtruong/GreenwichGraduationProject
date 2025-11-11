package com.example.demo.timtable.specializedTimetable.model;

import com.example.demo.classes.specializedClasses.model.SpecializedClasses;
import com.example.demo.timtable.majorTimetable.model.Timetable;
import com.example.demo.user.staff.model.Staffs;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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

    // === CONSTRUCTOR MỚI: DÙNG weekOfYear ===
    public SpecializedTimetable() {}

}