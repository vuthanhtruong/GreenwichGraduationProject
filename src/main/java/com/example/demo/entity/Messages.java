package com.example.demo.entity;

import com.example.demo.person.model.Persons;
import com.example.demo.entity.Enums.Notifications;
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

    @Id
    @Column(name = "MessageID", nullable = false)
    private String messageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MessageSenderID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Persons sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MessageRecipientID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Persons recipient;

    @Column(name = "Datetime", nullable = false)
    private LocalDateTime datetime;

    @Enumerated(EnumType.STRING)
    @Column(name = "Notification", nullable = true)
    private Notifications notification;

    @Column(name = "Text", nullable = true, length = 1000)
    private String text;

    public Messages() {}

    public Messages(String messageId, Persons sender, Persons recipient, LocalDateTime datetime) {
        if (sender == null || recipient == null || datetime == null) {
            throw new IllegalArgumentException("Sender, recipient, and datetime cannot be null");
        }
        this.messageId = messageId;
        this.sender = sender;
        this.recipient = recipient;
        this.datetime = datetime;
    }
}