package com.example.demo.dao;

import com.example.demo.entity.Majors;

import javax.security.auth.Subject;
import java.util.List;

public interface MajorDAO {
    Majors getByMajorName(String majorName);
    Majors getByMajorId(String majorId);
    List<Majors> getMajors();
}
