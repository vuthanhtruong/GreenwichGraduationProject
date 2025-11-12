// src/main/java/com/example/demo/timetable/minorTimetable/model/MinorTimetable.java
package com.example.demo.timtable.minorTimtable.model;

import com.example.demo.classes.minorClasses.model.MinorClasses;
import com.example.demo.timtable.majorTimetable.model.Timetable;
import com.example.demo.user.deputyStaff.model.DeputyStaffs;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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

    @Override
    public String getClassId() {
        return minorClass != null ? minorClass.getClassId() : null;
    }

    @Override
    public String getClassName() {
        return minorClass != null ? minorClass.getNameClass() : "Unknown";
    }

    @Override
    public String getClassType() {
        return "Minor";
    }

    @Override
    public String getCreatorName() {
        return creator != null ? creator.getFullName() : "N/A";
    }

    public MinorTimetable() {}
}