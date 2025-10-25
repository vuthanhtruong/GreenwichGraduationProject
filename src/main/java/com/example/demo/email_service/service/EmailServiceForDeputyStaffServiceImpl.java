package com.example.demo.email_service.service;

import com.example.demo.email_service.dao.EmailServiceForDeputyStaffDAO;
import com.example.demo.email_service.dto.DeputyStaffEmailContext;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceForDeputyStaffServiceImpl implements EmailServiceForDeputyStaffService {

    private final EmailServiceForDeputyStaffDAO emailServiceForDeputyStaffDAO;

    @Override
    public void sendEmailToNotifyLoginInformation(String to, String subject, DeputyStaffEmailContext context, String rawPassword) throws MessagingException {
        log.info("Service: Sending login information to deputy staff: {}", to);
        emailServiceForDeputyStaffDAO.sendEmailToNotifyLoginInformation(to, subject, context, rawPassword);
    }

    @Override
    public void sendEmailToNotifyInformationAfterEditing(String to, String subject, DeputyStaffEmailContext context) throws MessagingException {
        log.info("Service: Sending edit notification to deputy staff: {}", to);
        emailServiceForDeputyStaffDAO.sendEmailToNotifyInformationAfterEditing(to, subject, context);
    }

    @Override
    public void sendEmailToNotifyDeputyStaffDeletion(String to, String subject, DeputyStaffEmailContext context) throws MessagingException {
        log.info("Service: Sending deletion notification to deputy staff: {}", to);
        emailServiceForDeputyStaffDAO.sendEmailToNotifyDeputyStaffDeletion(to, subject, context);
    }
}