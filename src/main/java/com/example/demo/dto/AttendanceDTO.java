package com.example.demo.dto;

import java.time.LocalDateTime;

public class AttendanceDTO {
    private String attendanceId;
    private String studentName;
    private String markedByName;
    private String timetableId;
    private String status;
    private String note;
    private LocalDateTime createdAt;

    public AttendanceDTO(String attendanceId, String studentName, String markedByName,
                         String timetableId, String status, String note, LocalDateTime createdAt) {
        this.attendanceId = attendanceId;
        this.studentName = studentName;
        this.markedByName = markedByName;
        this.timetableId = timetableId;
        this.status = status;
        this.note = note;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public String getAttendanceId() { return attendanceId; }
    public void setAttendanceId(String attendanceId) { this.attendanceId = attendanceId; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public String getMarkedByName() { return markedByName; }
    public void setMarkedByName(String markedByName) { this.markedByName = markedByName; }
    public String getTimetableId() { return timetableId; }
    public void setTimetableId(String timetableId) { this.timetableId = timetableId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}