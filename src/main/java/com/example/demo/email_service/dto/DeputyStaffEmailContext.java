package com.example.demo.email_service.dto;

import java.time.LocalDate;

public record DeputyStaffEmailContext(
        String deputyStaffId,
        String fullName,
        String email,
        String phoneNumber,
        LocalDate birthDate,
        String gender,
        String fullAddress,
        String campusName,
        String creatorName,
        LocalDate createdDate
) {}