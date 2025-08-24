package com.example.demo.major.service;

import com.example.demo.major.model.Majors;

import java.util.List;

public interface MajorsService {
    Majors getByMajorName(String majorName);
    Majors getByMajorId(String majorId);
    List<Majors> getMajors();
}
