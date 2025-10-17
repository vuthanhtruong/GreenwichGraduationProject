package com.example.demo.entity;

import com.example.demo.entity.AbstractClasses.Attendance;
import com.example.demo.user.staff.model.Staffs;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "SpecializedAttendance")
@PrimaryKeyJoinColumn(name = "AttendanceID")
@Getter
@Setter
public class SpecializedAttendance extends Attendance {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TimetableID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private SpecializedTimetable timetable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MarkedBy", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Staffs markedBy;

    public SpecializedAttendance() {}

}