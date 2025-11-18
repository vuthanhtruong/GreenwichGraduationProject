package com.example.demo.messages.model;

import com.example.demo.user.person.model.Persons;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "Messages")
@Getter
@Setter
public class Messages {

    @Id
    @Column(name = "MessageID", nullable = false)
    private String messageId;

    // ← CÁC CỘT THÊM
    @Column(name = "MessageSenderID")
    private String messageSenderId;

    @Column(name = "MessageRecipientID")
    private String messageRecipientId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sender_id", nullable = false)
    private Persons sender;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "recipient_id", nullable = false)
    private Persons recipient;

    @Column(name = "Datetime", nullable = false)
    private LocalDateTime datetime;

    @Column(name = "Text", length = 1000)
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

        // ← TỰ ĐỘNG GÁN
        this.messageSenderId = sender.getId();
        this.messageRecipientId = recipient.getId();
    }
}