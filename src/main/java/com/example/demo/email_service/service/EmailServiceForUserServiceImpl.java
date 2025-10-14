package com.example.demo.email_service.service;

import com.example.demo.email_service.dao.EmailServiceForUserDAO;
import com.example.demo.email_service.dto.UserEmailContext;
import jakarta.mail.MessagingException;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceForUserServiceImpl implements EmailServiceForUserService{
    private final EmailServiceForUserDAO emailServiceForUserService;

    public EmailServiceForUserServiceImpl(EmailServiceForUserDAO emailServiceForUserService) {
        this.emailServiceForUserService = emailServiceForUserService;
    }

    @Override
    public void sendEmailForVerificationCode(String to, String subject, UserEmailContext context, String verificationCode) throws MessagingException {
        emailServiceForUserService.sendEmailForVerificationCode(to, subject, context, verificationCode);
    }
}
