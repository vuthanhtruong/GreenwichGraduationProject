package com.example.demo.email_service.dto;

import java.time.LocalDate;

public record MinorLecturerEmailContext(
        String lecturerId,
        String fullName,
        String email,
        String phoneNumber,
        LocalDate birthDate,
        String gender,
        String fullAddress,
        String campusName,
        String employmentType,
        LocalDate createdDate
) {}