package com.example.demo.email_service.dao;

import com.example.demo.student.model.Students;
import jakarta.mail.MessagingException;

public interface EmailServiceForStudentDAO {
    void sendEmailToNotifyLoginInformation(String to, String subject, Students students, String rawPassword) throws MessagingException;
    void sendEmailToNotifyInformationAfterEditing(String to, String subject, Students students) throws MessagingException;
}
