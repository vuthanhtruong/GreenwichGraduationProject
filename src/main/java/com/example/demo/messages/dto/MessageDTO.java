// src/main/java/com/example/demo/messages/dto/MessageDTO.java
package com.example.demo.messages.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {
    private String senderId;
    private String recipientId;
    private String text;
    private LocalDateTime datetime;
    private String senderDefaultAvatarPath;
}