package com.example.demo.email_service.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record AdminEmailContext(
        String adminId,
        String fullName,
        String email,
        String phoneNumber,
        LocalDate birthDate,
        String gender,
        String fullAddress,
        String campusName,
        String name,
        LocalDateTime createdDate) {}