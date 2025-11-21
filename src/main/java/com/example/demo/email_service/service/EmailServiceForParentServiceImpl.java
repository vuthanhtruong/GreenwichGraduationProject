package com.example.demo.email_service.service;

import com.example.demo.email_service.dao.EmailServiceForParentDAO;
import com.example.demo.email_service.dto.ParentEmailContext;
import jakarta.mail.MessagingException;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceForParentServiceImpl implements EmailServiceForParentService {
    private final EmailServiceForParentDAO emailServiceForParentDAO;

    public EmailServiceForParentServiceImpl(EmailServiceForParentDAO emailServiceForParentDAO) {
        this.emailServiceForParentDAO = emailServiceForParentDAO;
    }

    @Override
    public void sendEmailToNotifyLoginInformation(String to, String subject, ParentEmailContext context, String rawPassword) throws MessagingException {
        emailServiceForParentDAO.sendEmailToNotifyLoginInformation(to, subject, context, rawPassword);
    }

    @Override
    public void sendEmailToNotifyInformationAfterEditing(String to, String subject, ParentEmailContext context) throws MessagingException {
        emailServiceForParentDAO.sendEmailToNotifyInformationAfterEditing(to, subject, context);
    }

    @Override
    public void sendEmailToNotifyParentDeletion(String to, String subject, ParentEmailContext context) throws MessagingException {
        emailServiceForParentDAO.sendEmailToNotifyParentDeletion(to, subject, context);
    }

    @Override
    public void sendEmailToNotifyStudentLink(String to, String subject, ParentEmailContext context, String studentName, String studentId, String relationship) throws MessagingException {
        emailServiceForParentDAO.sendEmailToNotifyStudentLink(to, subject, context, studentName, studentId, relationship);
    }
}
