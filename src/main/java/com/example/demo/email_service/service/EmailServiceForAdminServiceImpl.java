package com.example.demo.email_service.service;

import com.example.demo.email_service.dao.EmailServiceForAdminDAO;
import com.example.demo.email_service.dto.AdminEmailContext;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceForAdminServiceImpl implements EmailServiceForAdminService {

    private final EmailServiceForAdminDAO emailServiceForAdminDAO;

    @Override
    public void sendEmailToNotifyLoginInformation(String to, String subject, AdminEmailContext context, String rawPassword) throws MessagingException {
        log.info("Service: Sending login information to admin: {}", to);
        emailServiceForAdminDAO.sendEmailToNotifyLoginInformation(to, subject, context, rawPassword);
    }

    @Override
    public void sendEmailToNotifyInformationAfterEditing(String to, String subject, AdminEmailContext context) throws MessagingException {
        log.info("Service: Sending edit notification to admin: {}", to);
        emailServiceForAdminDAO.sendEmailToNotifyInformationAfterEditing(to, subject, context);
    }

    @Override
    public void sendEmailToNotifyPasswordReset(String to, String subject, AdminEmailContext context, String newPassword) throws MessagingException {
        log.info("Service: Sending password reset notification to admin: {}", to);
        emailServiceForAdminDAO.sendEmailToNotifyPasswordReset(to, subject, context, newPassword);
    }
}