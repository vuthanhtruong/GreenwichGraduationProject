package com.example.demo.email_service.dto;

import java.time.LocalDate;

public record StudentEmailContext(
        String studentId,
        String fullName,
        String campusName,
        String majorName,
        String creatorName,
        LocalDate admissionYear,
        LocalDate createdDate,
        String learningProgramType,
        String avatarPath
) {}

