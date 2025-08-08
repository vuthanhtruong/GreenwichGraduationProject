package com.example.demo.service;



import com.example.demo.entity.*;

import java.util.List;

public interface StaffsService {
    Staffs getStaff();
    Majors getStaffMajor();
    List<MajorClasses> getClasses();
}
