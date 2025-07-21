package com.example.demo.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Embeddable
@Data
public class MessagesId implements Serializable {

    private String messageSenderId;
    private String messageRecipientId;
    private LocalDateTime datetime;

    // Constructor mặc định (yêu cầu cho JPA)
    public MessagesId() {}

    // Constructor với tham số
    public MessagesId(String messageSenderId, String messageRecipientId, LocalDateTime datetime) {
        this.messageSenderId = messageSenderId;
        this.messageRecipientId = messageRecipientId;
        this.datetime = datetime;
    }

    // Nếu không dùng Lombok, tự viết equals và hashCode
    /*
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MessagesId)) return false;
        MessagesId that = (MessagesId) o;
        return messageSenderId.equals(that.messageSenderId) &&
               messageRecipientId.equals(that.messageRecipientId) &&
               datetime.equals(that.datetime);
    }

    @Override
    public int hashCode() {
        return 31 * (31 * messageSenderId.hashCode() + messageRecipientId.hashCode()) + datetime.hashCode();
    }
    */
}