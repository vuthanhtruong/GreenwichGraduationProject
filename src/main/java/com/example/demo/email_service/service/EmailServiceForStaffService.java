package com.example.demo.email_service.service;

import com.example.demo.email_service.dto.StaffEmailContext;
import jakarta.mail.MessagingException;

public interface EmailServiceForStaffService {
    void sendEmailToNotifyLoginInformation(String to, String subject, StaffEmailContext context, String rawPassword) throws MessagingException;
    void sendEmailToNotifyInformationAfterEditing(String to, String subject, StaffEmailContext context) throws MessagingException;
    void sendEmailToNotifyStaffDeletion(String to, String subject, StaffEmailContext context) throws MessagingException;
}
