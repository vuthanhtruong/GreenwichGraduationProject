package com.example.demo.messages.dto;

import com.example.demo.messages.model.Messages;
import com.example.demo.user.person.model.Persons;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MessageDTO {
    private String id;
    private PersonDTO sender;
    private PersonDTO recipient;
    private String text;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime sentAt;

    public MessageDTO(Messages message) {
        this.id = message.getMessageId();
        this.sender = new PersonDTO(message.getSender());
        this.recipient = new PersonDTO(message.getRecipient());
        this.text = message.getText();
        this.sentAt = message.getDatetime();
    }

    @Getter
    @Setter
    public static class PersonDTO {
        private String id;
        private String fullName;
        private String avatar;

        public PersonDTO(Persons person) {
            this.id = person.getId();
            this.fullName = person.getFullName();
            this.avatar = person.getAvatar() != null ? "/persons/avatar/" + person.getId() : null;
        }
    }
}