package com.example.demo.dto;

import java.time.LocalDateTime;

public class OnlineRoomsDTO {
    private String roomId;
    private String roomName;
    private String link;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer slotQuantity;

    public OnlineRoomsDTO(String roomId, String roomName, String link, LocalDateTime startTime,
                          LocalDateTime endTime, Integer slotQuantity) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.link = link;
        this.startTime = startTime;
        this.endTime = endTime;
        this.slotQuantity = slotQuantity;
    }

    // Getters and Setters
    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }
    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }
    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public Integer getSlotQuantity() { return slotQuantity; }
    public void setSlotQuantity(Integer slotQuantity) { this.slotQuantity = slotQuantity; }
}