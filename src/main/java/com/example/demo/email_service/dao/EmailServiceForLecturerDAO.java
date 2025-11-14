package com.example.demo.email_service.dao;

import com.example.demo.email_service.dto.LecturerEmailContext;
import jakarta.mail.MessagingException;
import org.springframework.scheduling.annotation.Async;

public interface EmailServiceForLecturerDAO {
    void sendEmailToNotifyLoginInformation(String to, String subject, LecturerEmailContext context, String rawPassword) throws MessagingException;
    void sendEmailToNotifyInformationAfterEditing(String to, String subject, LecturerEmailContext context) throws MessagingException;

    @Async("emailTaskExecutor")
    void sendEmailToNotifyLecturerDeletion(String to, String subject, LecturerEmailContext context) throws MessagingException;
}