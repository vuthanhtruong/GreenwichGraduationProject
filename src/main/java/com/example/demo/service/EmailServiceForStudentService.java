package com.example.demo.service;

import com.example.demo.entity.Students;
import jakarta.mail.MessagingException;

public interface EmailServiceForStudentService {
    void sendEmailToNotifyLoginInformation(String to, String subject, Students students, String rawPassword) throws MessagingException;
    void sendEmailToNotifyInformationAfterEditing(String to, String subject, Students students) throws MessagingException;
}
