package com.example.demo.entity.AbstractClasses;

import com.example.demo.entity.Enums.AttendanceStatus;
import com.example.demo.student.model.Students;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "Attendance")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
public abstract class Attendance {

    @Id
    @Column(name = "AttendanceID")
    private String attendanceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "StudentID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Students student;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status", nullable = true, length = 50)
    private AttendanceStatus status;

    @Column(name = "Note", nullable = true, length = 500)
    private String note;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;
}