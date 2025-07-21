package com.example.demo.dto;

import java.time.LocalDate;

public class TimetableDTO {
    private String timetableId;
    private String roomName;
    private String className;
    private String slotName;
    private String dayOfTheWeek;
    private LocalDate date;

    public TimetableDTO(String timetableId, String roomName, String className, String slotName,
                        String dayOfTheWeek, LocalDate date) {
        this.timetableId = timetableId;
        this.roomName = roomName;
        this.className = className;
        this.slotName = slotName;
        this.dayOfTheWeek = dayOfTheWeek;
        this.date = date;
    }

    // Getters and Setters
    public String getTimetableId() { return timetableId; }
    public void setTimetableId(String timetableId) { this.timetableId = timetableId; }
    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
    public String getSlotName() { return slotName; }
    public void setSlotName(String slotName) { this.slotName = slotName; }
    public String getDayOfTheWeek() { return dayOfTheWeek; }
    public void setDayOfTheWeek(String dayOfTheWeek) { this.dayOfTheWeek = dayOfTheWeek; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
}