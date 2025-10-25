package com.example.demo.email_service.service;

import com.example.demo.email_service.dto.MinorLecturerEmailContext;
import jakarta.mail.MessagingException;

public interface EmailServiceForMinorLecturerService {
    void sendEmailToNotifyLoginInformation(String to, String subject, MinorLecturerEmailContext context, String rawPassword) throws MessagingException;
    void sendEmailToNotifyInformationAfterEditing(String to, String subject, MinorLecturerEmailContext context) throws MessagingException;
    void sendEmailToNotifyMinorLecturerDeletion(String to, String subject, MinorLecturerEmailContext context) throws MessagingException;
}
