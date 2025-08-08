package com.example.demo.service.impl;

import com.example.demo.dao.EmailServiceForLectureDAO;
import com.example.demo.entity.MajorLecturers;
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
    public void sendEmailToNotifyInformationAfterEditing(String to, String subject, MajorLecturers teacher) throws MessagingException {
        emailServiceForLectureServiceDAO.sendEmailToNotifyInformationAfterEditing(to, subject, teacher);
    }

    @Override
    public void sendEmailToNotifyLoginInformation(String to, String subject, MajorLecturers teacher, String rawPassword) throws MessagingException {
        emailServiceForLectureServiceDAO.sendEmailToNotifyLoginInformation(to, subject, teacher, rawPassword);
    }
}
