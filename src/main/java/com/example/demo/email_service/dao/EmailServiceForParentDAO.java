package com.example.demo.email_service.dao;

import com.example.demo.email_service.dto.ParentEmailContext;
import jakarta.mail.MessagingException;

public interface EmailServiceForParentDAO {

    /**
     * Gửi email thông báo thông tin đăng nhập cho phụ huynh mới
     */
    void sendEmailToNotifyLoginInformation(String to, String subject,
                                           ParentEmailContext context,
                                           String rawPassword) throws MessagingException;

    /**
     * Gửi email thông báo sau khi chỉnh sửa thông tin phụ huynh
     */
    void sendEmailToNotifyInformationAfterEditing(String to, String subject,
                                                  ParentEmailContext context) throws MessagingException;

    /**
     * Gửi email thông báo xóa tài khoản phụ huynh
     */
    void sendEmailToNotifyParentDeletion(String to, String subject,
                                         ParentEmailContext context) throws MessagingException;

    /**
     * Gửi email thông báo liên kết với học sinh
     */
    void sendEmailToNotifyStudentLink(String to, String subject,
                                      ParentEmailContext context,
                                      String studentName,
                                      String studentId,
                                      String relationship) throws MessagingException;
}