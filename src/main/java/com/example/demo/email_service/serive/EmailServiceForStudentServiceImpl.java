package com.example.demo.email_service.serive;

import com.example.demo.email_service.dao.EmailServiceForStudentDAO;
import com.example.demo.student.model.Students;
import jakarta.mail.MessagingException;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceForStudentServiceImpl implements EmailServiceForStudentService {

    private final EmailServiceForStudentDAO emailServiceForStudentDAO;

    public EmailServiceForStudentServiceImpl(EmailServiceForStudentDAO emailServiceForStudentDAO) {
        this.emailServiceForStudentDAO = emailServiceForStudentDAO;
    }

    @Override
    public void sendEmailToNotifyLoginInformation(String to, String subject, Students students, String rawPassword) throws MessagingException {
        emailServiceForStudentDAO.sendEmailToNotifyLoginInformation(to, subject, students, rawPassword);
    }

    @Override
    public void sendEmailToNotifyInformationAfterEditing(String to, String subject, Students students) throws MessagingException {
        emailServiceForStudentDAO.sendEmailToNotifyInformationAfterEditing(to, subject, students);
    }
}
