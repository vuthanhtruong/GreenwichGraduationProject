package com.example.demo.majorstaff.dao;

import com.example.demo.classes.model.MajorClasses;
import com.example.demo.major.model.Majors;
import com.example.demo.majorstaff.model.Staffs;

import java.util.List;

public interface StaffsDAO {
    Staffs getStaff();
    Majors getStaffMajor();
    List<MajorClasses> getClasses();
}