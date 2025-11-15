package com.example.demo.attendance.specializedAttendance.model;

import com.example.demo.attendance.majorAttendance.model.Attendance;
import com.example.demo.timetable.specializedTimetable.model.SpecializedTimetable;
import com.example.demo.user.employe.model.MajorEmployes;
import com.example.demo.entity.Enums.YourNotification;
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
    private MajorEmployes markedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", length = 50, nullable = false)
    private YourNotification notificationType;

    public SpecializedAttendance() {
        this.notificationType = YourNotification.NOTIFICATION_013;
    }
}
