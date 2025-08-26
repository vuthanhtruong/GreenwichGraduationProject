package com.example.demo.email_service.service;

import com.example.demo.lecturer.model.MajorLecturers;
import jakarta.mail.MessagingException;

public interface EmailServiceForLectureService {
    void sendEmailToNotifyInformationAfterEditing(String to, String subject, MajorLecturers teacher) throws MessagingException;
    void sendEmailToNotifyLoginInformation(String to, String subject, MajorLecturers teacher, String rawPassword) throws MessagingException;
}
