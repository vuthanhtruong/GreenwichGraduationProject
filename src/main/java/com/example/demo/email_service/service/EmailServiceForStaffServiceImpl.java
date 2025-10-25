package com.example.demo.email_service.service;

import com.example.demo.email_service.dao.EmailServiceForStaffDAO;
import com.example.demo.email_service.dto.StaffEmailContext;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceForStaffServiceImpl implements EmailServiceForStaffService {

    private final EmailServiceForStaffDAO emailServiceForStaffDAO;

    @Override
    public void sendEmailToNotifyLoginInformation(String to, String subject, StaffEmailContext context, String rawPassword) throws MessagingException {
        log.info("Service: Sending login information to staff: {}", to);
        emailServiceForStaffDAO.sendEmailToNotifyLoginInformation(to, subject, context, rawPassword);
    }

    @Override
    public void sendEmailToNotifyInformationAfterEditing(String to, String subject, StaffEmailContext context) throws MessagingException {
        log.info("Service: Sending edit notification to staff: {}", to);
        emailServiceForStaffDAO.sendEmailToNotifyInformationAfterEditing(to, subject, context);
    }

    @Override
    public void sendEmailToNotifyStaffDeletion(String to, String subject, StaffEmailContext context) throws MessagingException {
        log.info("Service: Sending deletion notification to staff: {}", to);
        emailServiceForStaffDAO.sendEmailToNotifyStaffDeletion(to, subject, context);
    }
}