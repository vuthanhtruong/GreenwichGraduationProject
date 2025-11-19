package com.example.demo.email_service.service;

import com.example.demo.email_service.dao.EmailServiceForStudentDAO;
import com.example.demo.email_service.dto.ScheduleEmailContext;
import com.example.demo.email_service.dto.StudentEmailContext;
import jakarta.mail.MessagingException;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceForStudentServiceImpl implements EmailServiceForStudentService {
    @Override
    public void sendEmailToNotifyStudentDeletion(String to, String subject, StudentEmailContext context) throws MessagingException {
        emailServiceForStudentDAO.sendEmailToNotifyStudentDeletion(to, subject, context);
    }

    @Override
    public void sendScheduleNotificationEmail(String to, String subject, ScheduleEmailContext context) throws MessagingException {
        emailServiceForStudentDAO.sendScheduleNotificationEmail(to, subject, context);
    }

    @Override
    public void sendEmailToNotifyLoginInformation(String to, String subject, StudentEmailContext context, String rawPassword) throws MessagingException {
        emailServiceForStudentDAO.sendEmailToNotifyLoginInformation(to, subject, context, rawPassword);
    }

    @Override
    public void sendEmailToNotifyInformationAfterEditing(String to, String subject, StudentEmailContext context) throws MessagingException {
        emailServiceForStudentDAO.sendEmailToNotifyInformationAfterEditing(to, subject, context);
    }

    private final EmailServiceForStudentDAO emailServiceForStudentDAO;

    public EmailServiceForStudentServiceImpl(EmailServiceForStudentDAO emailServiceForStudentDAO) {
        this.emailServiceForStudentDAO = emailServiceForStudentDAO;
    }

}
