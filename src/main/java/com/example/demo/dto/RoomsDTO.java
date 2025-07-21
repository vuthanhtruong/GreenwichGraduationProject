package com.example.demo.dto;

import java.time.LocalDateTime;

public class RoomsDTO {
    private String roomId;
    private String roomName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer slotQuantity;

    public RoomsDTO(String roomId, String roomName, LocalDateTime startTime, LocalDateTime endTime,
                    Integer slotQuantity) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.slotQuantity = slotQuantity;
    }

    // Getters and Setters
    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }
    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public Integer getSlotQuantity() { return slotQuantity; }
    public void setSlotQuantity(Integer slotQuantity) { this.slotQuantity = slotQuantity; }
}