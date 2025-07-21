package com.example.demo.dto;

import java.time.LocalDateTime;

public class MessagesDTO {
    private String senderId;
    private String recipientId;
    private String senderName;
    private String recipientName;
    private String text;
    private LocalDateTime datetime;

    public MessagesDTO(String senderId, String recipientId, String senderName, String recipientName,
                       String text, LocalDateTime datetime) {
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.senderName = senderName;
        this.recipientName = recipientName;
        this.text = text;
        this.datetime = datetime;
    }

    // Getters and Setters
    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }
    public String getRecipientId() { return recipientId; }
    public void setRecipientId(String recipientId) { this.recipientId = recipientId; }
    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }
    public String getRecipientName() { return recipientName; }
    public void setRecipientName(String recipientName) { this.recipientName = recipientName; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public LocalDateTime getDatetime() { return datetime; }
    public void setDatetime(LocalDateTime datetime) { this.datetime = datetime; }
}