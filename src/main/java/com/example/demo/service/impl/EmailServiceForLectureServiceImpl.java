package com.example.demo.service.impl;

import com.example.demo.dao.EmailServiceForLectureDAO;
import com.example.demo.dao.EmailServiceForStudentDAO;
import com.example.demo.entity.Lecturers;
import com.example.demo.service.EmailServiceForLectureService;
import jakarta.mail.MessagingException;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceForLectureServiceImpl implements EmailServiceForLectureService {

    private final EmailServiceForLectureDAO emailServiceForLectureServiceDAO;

    public EmailServiceForLectureServiceImpl(EmailServiceForLectureDAO emailServiceForLectureServiceDAO) {
        this.emailServiceForLectureServiceDAO = emailServiceForLectureServiceDAO;
    }

    @Override
    public void sendEmailToNotifyInformationAfterEditing(String to, String subject, Lecturers teacher) throws MessagingException {
        emailServiceForLectureServiceDAO.sendEmailToNotifyInformationAfterEditing(to, subject, teacher);
    }

    @Override
    public void sendEmailToNotifyLoginInformation(String to, String subject, Lecturers teacher, String rawPassword) throws MessagingException {
        emailServiceForLectureServiceDAO.sendEmailToNotifyLoginInformation(to, subject, teacher, rawPassword);
    }
}
