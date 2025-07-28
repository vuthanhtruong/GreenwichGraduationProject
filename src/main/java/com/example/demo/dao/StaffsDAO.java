package com.example.demo.dao;

import com.example.demo.entity.Classes;
import com.example.demo.entity.Majors;
import com.example.demo.entity.Staffs;

import java.util.List;

public interface StaffsDAO {
    Staffs getStaffs();
    Majors getMajors();
    List<Classes> getClasses();
}