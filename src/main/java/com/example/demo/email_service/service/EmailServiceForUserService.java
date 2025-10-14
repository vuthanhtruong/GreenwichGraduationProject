package com.example.demo.email_service.service;

import com.example.demo.email_service.dto.UserEmailContext;
import jakarta.mail.MessagingException;

public interface EmailServiceForUserService {
    void sendEmailForVerificationCode(String to, String subject, UserEmailContext context, String verificationCode) throws MessagingException;
}
