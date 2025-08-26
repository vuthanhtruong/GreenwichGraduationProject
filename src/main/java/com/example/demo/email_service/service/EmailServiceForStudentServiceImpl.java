package com.example.demo.email_service.service;

import com.example.demo.email_service.dao.EmailServiceForStudentDAO;
import com.example.demo.email_service.dto.StudentEmailContext;
import com.example.demo.student.model.Students;
import jakarta.mail.MessagingException;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceForStudentServiceImpl implements EmailServiceForStudentService {
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
