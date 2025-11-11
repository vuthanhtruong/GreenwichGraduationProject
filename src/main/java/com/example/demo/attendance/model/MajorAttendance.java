package com.example.demo.attendance.model;

import com.example.demo.timtable.majorTimetable.model.MajorTimetable;
import com.example.demo.user.employe.model.MajorEmployes;
import com.example.demo.entity.Enums.AttendanceStatus;
import com.example.demo.user.student.model.Students;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "MajorAttendance")
@PrimaryKeyJoinColumn(name = "AttendanceID")
@Getter
@Setter
public class MajorAttendance extends Attendance {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TimetableID", nullable = false, insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MajorTimetable timetable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MarkedByID", nullable = false, insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MajorEmployes markedBy;

    public MajorAttendance() {}

    public MajorAttendance(String attendanceId, Students student, MajorEmployes markedBy, MajorTimetable timetable, AttendanceStatus status, String note, LocalDateTime createdAt) {
        super.setAttendanceId(attendanceId);
        super.setStudent(student);
        this.markedBy = markedBy;
        this.timetable = timetable;
        super.setStatus(status);
        super.setNote(note);
        super.setCreatedAt(createdAt != null ? createdAt : LocalDateTime.now());
    }
}