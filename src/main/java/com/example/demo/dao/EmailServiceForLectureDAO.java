package com.example.demo.dao;

import com.example.demo.entity.MajorLecturers;
import jakarta.mail.MessagingException;

public interface EmailServiceForLectureDAO {
    void sendEmailToNotifyInformationAfterEditing(String to, String subject, MajorLecturers teacher) throws MessagingException;
    void sendEmailToNotifyLoginInformation(String to, String subject, MajorLecturers teacher, String rawPassword) throws MessagingException;
}
