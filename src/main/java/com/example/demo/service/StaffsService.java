package com.example.demo.service;


import com.example.demo.dto.StudentsDTO;
import com.example.demo.entity.*;
import jakarta.mail.MessagingException;

import java.util.List;

public interface StaffsService {
    Staffs getStaff();
    Majors getStaffMajor();
    List<Classes> getClasses();
}
