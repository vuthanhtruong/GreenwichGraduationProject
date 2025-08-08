package com.example.demo.dao;

import com.example.demo.entity.MajorClasses;
import com.example.demo.entity.Majors;
import com.example.demo.entity.Staffs;

import java.util.List;

public interface StaffsDAO {
    Staffs getStaff();
    Majors getStaffMajor();
    List<MajorClasses> getClasses();
}