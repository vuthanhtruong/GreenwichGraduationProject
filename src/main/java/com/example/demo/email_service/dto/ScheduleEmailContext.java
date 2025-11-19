package com.example.demo.email_service.dto;

import java.time.LocalDate;

// package com.example.demo.email_service.dto;
public record ScheduleEmailContext(
        String studentId,
        String fullName,
        String email,
        String ClassName,
        String SubjectName
) {}