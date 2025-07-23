package com.example.demo.dao;

import com.example.demo.entity.Classes;
import com.example.demo.entity.Majors;
import com.example.demo.entity.Staffs;

import java.util.List;

public interface StaffsDAO {
    Staffs getStaffs();
    Majors getMajors();
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsPersonById(String id);
    boolean existsByEmailExcludingId(String email, String id);
    boolean existsByPhoneNumberExcludingId(String phoneNumber, String id);
    List<Classes> getClasses();
}