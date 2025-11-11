// src/main/java/com/example/demo/timetable/majorTimetable/model/MajorTimetable.java
package com.example.demo.timtable.majorTimetable.model;

import com.example.demo.classes.majorClasses.model.MajorClasses;
import com.example.demo.user.staff.model.Staffs;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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

    @Override
    public String getClassId() {
        return classEntity != null ? classEntity.getClassId() : null;
    }

    @Override
    public String getClassName() {
        return classEntity != null ? classEntity.getNameClass() : "Unknown";
    }

    @Override
    public String getClassType() {
        return "Major";
    }

    @Override
    public String getCreatorName() {
        return creator != null ? creator.getFullName() : "N/A";
    }

    public MajorTimetable() {}
}