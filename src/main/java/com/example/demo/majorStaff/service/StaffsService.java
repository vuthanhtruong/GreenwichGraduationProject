package com.example.demo.majorStaff.service;



import com.example.demo.classes.model.MajorClasses;
import com.example.demo.major.model.Majors;
import com.example.demo.majorStaff.model.Staffs;

import java.util.List;

public interface StaffsService {
    Staffs getStaff();
    Majors getStaffMajor();
    List<MajorClasses> getClasses();
}
