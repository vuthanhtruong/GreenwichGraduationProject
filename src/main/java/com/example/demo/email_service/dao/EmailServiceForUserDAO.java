package com.example.demo.email_service.dao;

import com.example.demo.email_service.dto.UserEmailContext;
import jakarta.mail.MessagingException;

public interface EmailServiceForUserDAO {
    void sendEmailForVerificationCode(String to, String subject, UserEmailContext context, String verificationCode) throws MessagingException;
}