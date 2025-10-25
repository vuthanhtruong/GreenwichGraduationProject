package com.example.demo.email_service.service;

import com.example.demo.email_service.dao.EmailServiceForMinorLecturerDAO;
import com.example.demo.email_service.dto.MinorLecturerEmailContext;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceForMinorLecturerServiceImpl implements EmailServiceForMinorLecturerService {

    private final EmailServiceForMinorLecturerDAO emailServiceForMinorLecturerDAO;

    @Override
    public void sendEmailToNotifyLoginInformation(String to, String subject, MinorLecturerEmailContext context, String rawPassword) throws MessagingException {
        log.info("Service: Sending login information to minor lecturer: {}", to);
        emailServiceForMinorLecturerDAO.sendEmailToNotifyLoginInformation(to, subject, context, rawPassword);
    }

    @Override
    public void sendEmailToNotifyInformationAfterEditing(String to, String subject, MinorLecturerEmailContext context) throws MessagingException {
        log.info("Service: Sending edit notification to minor lecturer: {}", to);
        emailServiceForMinorLecturerDAO.sendEmailToNotifyInformationAfterEditing(to, subject, context);
    }

    @Override
    public void sendEmailToNotifyMinorLecturerDeletion(String to, String subject, MinorLecturerEmailContext context) throws MessagingException {
        log.info("Service: Sending deletion notification to minor lecturer: {}", to);
        emailServiceForMinorLecturerDAO.sendEmailToNotifyMinorLecturerDeletion(to, subject, context);
    }
}