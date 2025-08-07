package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "Attendance")
@Getter
@Setter
public class Attendance {

    @Id
    @Column(name = "AttendanceID")
    private String attendanceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "StudentID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Students student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MarkedByID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Employes markedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TimetableID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Timetable timetable;

    @Column(name = "Status", nullable = true, length = 50)
    private String status;

    @Column(name = "Note", nullable = true, length = 500)
    private String note;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;
}