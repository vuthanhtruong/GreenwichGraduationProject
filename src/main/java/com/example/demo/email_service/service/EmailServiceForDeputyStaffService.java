package com.example.demo.email_service.service;

import com.example.demo.email_service.dto.DeputyStaffEmailContext;
import jakarta.mail.MessagingException;

public interface EmailServiceForDeputyStaffService {
    void sendEmailToNotifyLoginInformation(String to, String subject, DeputyStaffEmailContext context, String rawPassword) throws MessagingException;
    void sendEmailToNotifyInformationAfterEditing(String to, String subject, DeputyStaffEmailContext context) throws MessagingException;
    void sendEmailToNotifyDeputyStaffDeletion(String to, String subject, DeputyStaffEmailContext context) throws MessagingException;
}
