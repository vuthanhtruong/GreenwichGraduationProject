package com.example.demo.email_service.dto;

import java.time.LocalDate;

public record StaffEmailContext(
        String staffId,
        String fullName,
        String email,
        String phoneNumber,
        LocalDate birthDate,
        String gender,
        String fullAddress,
        String campusName,
        String majorName,
        String creatorName,
        LocalDate createdDate
) {}