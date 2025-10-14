package com.example.demo.email_service.dto;

import java.time.LocalDate;

public record StudentEmailContext(
        String studentId,
        String fullName,
        String email,
        String phoneNumber,
        LocalDate birthDate,
        String gender,
        String fullAddress,
        String campusName,
        String majorName,
        String creatorName,
        LocalDate admissionYear,
        LocalDate createdDate,
        String learningProgramType
) {}