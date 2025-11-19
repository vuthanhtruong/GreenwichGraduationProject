package com.example.demo.email_service.service;

import com.example.demo.email_service.dto.ScheduleEmailContext;
import com.example.demo.email_service.dto.StudentEmailContext;
import jakarta.mail.MessagingException;
import org.springframework.scheduling.annotation.Async;

public interface EmailServiceForStudentService {
    void sendEmailToNotifyLoginInformation(String to, String subject, StudentEmailContext context, String rawPassword) throws MessagingException;
    void sendEmailToNotifyInformationAfterEditing(String to, String subject, StudentEmailContext context) throws MessagingException;

    @Async("emailTaskExecutor")
    void sendEmailToNotifyStudentDeletion(String to, String subject, StudentEmailContext context) throws MessagingException;

    // EmailServiceForStudentDAO.java
    @Async("emailTaskExecutor")
    void sendScheduleNotificationEmail(String to, String subject, ScheduleEmailContext context) throws MessagingException;
}
