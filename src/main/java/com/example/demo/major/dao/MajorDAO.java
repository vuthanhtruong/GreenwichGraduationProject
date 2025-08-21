package com.example.demo.major.dao;

import com.example.demo.major.model.Majors;

import java.util.List;

public interface MajorDAO {
    Majors getByMajorName(String majorName);
    Majors getByMajorId(String majorId);
    List<Majors> getMajors();
}
