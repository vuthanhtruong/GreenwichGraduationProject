package com.example.demo.email_service.service;

import com.example.demo.email_service.dto.AdminEmailContext;
import jakarta.mail.MessagingException;

public interface EmailServiceForAdminService {
    void sendEmailToNotifyLoginInformation(String to, String subject, AdminEmailContext context, String rawPassword) throws MessagingException;
    void sendEmailToNotifyInformationAfterEditing(String to, String subject, AdminEmailContext context) throws MessagingException;
    void sendEmailToNotifyPasswordReset(String to, String subject, AdminEmailContext context, String newPassword) throws MessagingException;
}
