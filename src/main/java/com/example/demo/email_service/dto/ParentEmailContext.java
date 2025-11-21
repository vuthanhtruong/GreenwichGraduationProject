package com.example.demo.email_service.dto;

import java.time.LocalDate;

/**
 * Context object chứa thông tin phụ huynh để gửi email
 */
public record ParentEmailContext(
        String parentId,
        String fullName,
        String email,
        String phoneNumber,
        LocalDate birthDate,
        String gender,
        String fullAddress,
        String creatorName,
        LocalDate createdDate
) {
}