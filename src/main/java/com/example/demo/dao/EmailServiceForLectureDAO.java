package com.example.demo.dao;

import com.example.demo.entity.Lecturers;
import jakarta.mail.MessagingException;

public interface EmailServiceForLectureDAO {
    void sendEmailToNotifyInformationAfterEditing(String to, String subject, Lecturers teacher) throws MessagingException;
    void sendEmailToNotifyLoginInformation(String to, String subject, Lecturers teacher, String rawPassword) throws MessagingException;
}
