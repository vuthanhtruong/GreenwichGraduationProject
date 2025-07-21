package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "Messages")
@Getter
@Setter
public class Messages {

    @EmbeddedId
    private MessagesId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("messageSenderId")
    @JoinColumn(name = "MessageSenderID")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Persons sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("messageRecipientId")
    @JoinColumn(name = "MessageRecipientID")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Persons recipient;

    @Column(name = "Datetime", nullable = false)
    private LocalDateTime datetime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NotificationID", nullable = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Notifications notification;

    @Column(name = "Text", nullable = true, length = 1000)
    private String text;

    @Embeddable
    public static class MessagesId {
        private String messageSenderId;
        private String messageRecipientId;
        private LocalDateTime datetime;
    }
}