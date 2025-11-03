// src/main/java/com/example/demo/messages/model/MessageRequest.java
package com.example.demo.messages.dto;

public class MessageRequest {
    private String recipientId;
    private String text;

    public String getRecipientId() { return recipientId; }
    public void setRecipientId(String recipientId) { this.recipientId = recipientId; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
}