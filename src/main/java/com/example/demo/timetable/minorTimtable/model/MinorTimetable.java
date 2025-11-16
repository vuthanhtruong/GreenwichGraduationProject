// src/main/java/com/example/demo/timetable/minorTimetable/model/MinorTimetable.java
package com.example.demo.timetable.minorTimtable.model;

import com.example.demo.attendance.minorAttendance.model.MinorAttendance;
import com.example.demo.classes.minorClasses.model.MinorClasses;
import com.example.demo.timetable.majorTimetable.model.Timetable;
import com.example.demo.user.deputyStaff.model.DeputyStaffs;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "MinorTimetable")
@PrimaryKeyJoinColumn(name = "TimetableID")
@Getter
@Setter
public class MinorTimetable extends Timetable {
    @Override
    public String getDetailUrl() {
        return "";
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MinorClassID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MinorClasses minorClass;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Creator", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private DeputyStaffs creator;

    @OneToMany(mappedBy = "timetable", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<MinorAttendance> attendances = new ArrayList<>();

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

    @Override
    public String getAttendanceByStudentId(String id) {
        for (MinorAttendance attendance : attendances) {
            if (attendance.getStudent().getId().equals(id)) {
                return attendance.getStatus().toString();
            }
        }
        return null;
    }

    public MinorTimetable() {}
}