// src/main/java/com/example/demo/timetable/specializedTimetable/model/SpecializedTimetable.java
package com.example.demo.timetable.specializedTimetable.model;

import com.example.demo.attendance.majorAttendance.model.MajorAttendance;
import com.example.demo.attendance.specializedAttendance.model.SpecializedAttendance;
import com.example.demo.classes.specializedClasses.model.SpecializedClasses;
import com.example.demo.specialization.model.Specialization;
import com.example.demo.timetable.majorTimetable.model.Timetable;
import com.example.demo.user.staff.model.Staffs;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "timetable", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<SpecializedAttendance> attendances = new ArrayList<>();

    @Override
    public String getClassId() {
        return specializedClass != null ? specializedClass.getClassId() : null;
    }

    @Override
    public String getClassName() {
        return specializedClass != null ? specializedClass.getNameClass() : "Unknown";
    }

    @Override
    public String getClassType() {
        return "Specialized";
    }

    @Override
    public String getCreatorName() {
        return creator != null ? creator.getFullName() : "N/A";
    }

    @Override
    public String getDetailUrl() {
        return "/specialized-timetable/detail";
    }

    @Override
    public String getAttendanceByStudentId(String id) {
        for (SpecializedAttendance attendance : attendances) {
            if (attendance.getStudent().getId().equals(id)) {
                return attendance.getStatus().toString();
            }
        }
        return null;
    }

    public SpecializedTimetable() {}
}