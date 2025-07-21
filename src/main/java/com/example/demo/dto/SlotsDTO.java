package com.example.demo.dto;

import java.time.LocalTime;

public class SlotsDTO {
    private String slotId;
    private String slotName;
    private LocalTime startTime;
    private LocalTime endTime;

    public SlotsDTO(String slotId, String slotName, LocalTime startTime, LocalTime endTime) {
        this.slotId = slotId;
        this.slotName = slotName;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Getters and Setters
    public String getSlotId() { return slotId; }
    public void setSlotId(String slotId) { this.slotId = slotId; }
    public String getSlotName() { return slotName; }
    public void setSlotName(String slotName) { this.slotName = slotName; }
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
}