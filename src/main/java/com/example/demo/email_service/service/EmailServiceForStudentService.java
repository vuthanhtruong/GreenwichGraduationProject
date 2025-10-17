package com.example.demo.email_service.service;

import com.example.demo.email_service.dto.StudentEmailContext;
import jakarta.mail.MessagingException;

public interface EmailServiceForStudentService {
    void sendEmailToNotifyLoginInformation(String to, String subject, StudentEmailContext context, String rawPassword) throws MessagingException;
    void sendEmailToNotifyInformationAfterEditing(String to, String subject, StudentEmailContext context) throws MessagingException;
}
