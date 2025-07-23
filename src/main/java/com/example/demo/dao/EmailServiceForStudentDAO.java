package com.example.demo.dao;

import com.example.demo.entity.Students;
import jakarta.mail.MessagingException;

public interface EmailServiceForStudentDAO {
    void sendEmailToNotifyLoginInformation(String to, String subject, Students students, String rawPassword) throws MessagingException;
    void sendEmailToNotifyInformationAfterEditing(String to, String subject, Students students) throws MessagingException;
}
