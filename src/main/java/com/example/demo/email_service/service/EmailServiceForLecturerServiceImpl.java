package com.example.demo.email_service.service;

import com.example.demo.email_service.dao.EmailServiceForLecturerDAO;
import com.example.demo.email_service.dto.LecturerEmailContext;
import jakarta.mail.MessagingException;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceForLecturerServiceImpl implements EmailServiceForLecturerService {
    @Override
    public void sendEmailToNotifyLoginInformation(String to, String subject, LecturerEmailContext context, String rawPassword) throws MessagingException {
        emailServiceForLectureServiceDAO.sendEmailToNotifyLoginInformation(to, subject, context, rawPassword);
    }

    @Override
    public void sendEmailToNotifyInformationAfterEditing(String to, String subject, LecturerEmailContext context) throws MessagingException {
        emailServiceForLectureServiceDAO.sendEmailToNotifyInformationAfterEditing(to, subject, context);
    }

    private final EmailServiceForLecturerDAO emailServiceForLectureServiceDAO;

    public EmailServiceForLecturerServiceImpl(EmailServiceForLecturerDAO emailServiceForLectureServiceDAO) {
        this.emailServiceForLectureServiceDAO = emailServiceForLectureServiceDAO;
    }

}
