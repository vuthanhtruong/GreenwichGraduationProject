package com.example.demo.email_service.dto;

import java.time.LocalDate;

public record UserEmailContext(
        String id,
        String fullName,
        String email,
        String phoneNumber,
        LocalDate birthDate,
        String gender,
        String fullAddress,
        String campusName,
        String majorName,
        LocalDate createdDate,
        String creatorName
        // No avatarPath field
) {}