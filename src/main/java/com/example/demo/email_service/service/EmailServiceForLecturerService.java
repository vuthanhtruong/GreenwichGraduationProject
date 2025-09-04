package com.example.demo.email_service.service;

import com.example.demo.email_service.dto.LecturerEmailContext;
import jakarta.mail.MessagingException;

public interface EmailServiceForLecturerService {
    void sendEmailToNotifyLoginInformation(String to, String subject, LecturerEmailContext context, String rawPassword) throws MessagingException;
    void sendEmailToNotifyInformationAfterEditing(String to, String subject, LecturerEmailContext context) throws MessagingException;
}
