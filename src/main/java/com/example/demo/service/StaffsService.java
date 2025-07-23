package com.example.demo.service;


import com.example.demo.dto.StudentsDTO;
import com.example.demo.entity.*;
import jakarta.mail.MessagingException;

import java.util.List;

public interface StaffsService {
    Staffs getStaffs();
    Majors getMajors();
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsPersonById(String id);
    boolean existsByEmailExcludingId(String email, String id);
    boolean existsByPhoneNumberExcludingId(String phoneNumber, String id);
    List<Classes> getClasses();
}
