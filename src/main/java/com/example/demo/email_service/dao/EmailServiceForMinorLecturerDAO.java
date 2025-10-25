package com.example.demo.email_service.dao;

import com.example.demo.email_service.dto.MinorLecturerEmailContext;
import jakarta.mail.MessagingException;

public interface EmailServiceForMinorLecturerDAO {
    void sendEmailToNotifyLoginInformation(String to, String subject, MinorLecturerEmailContext context, String rawPassword) throws MessagingException;
    void sendEmailToNotifyInformationAfterEditing(String to, String subject, MinorLecturerEmailContext context) throws MessagingException;
    void sendEmailToNotifyMinorLecturerDeletion(String to, String subject, MinorLecturerEmailContext context) throws MessagingException;
}
