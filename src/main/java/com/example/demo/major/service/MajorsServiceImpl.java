package com.example.demo.major.service;

import com.example.demo.major.dao.MajorDAO;
import com.example.demo.major.model.Majors;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MajorsServiceImpl implements MajorsService{
    private final MajorDAO majorDAO;

    public MajorsServiceImpl(MajorDAO majorDAO) {
        this.majorDAO = majorDAO;
    }


    @Override
    public Majors getByMajorName(String majorName) {
        return majorDAO.getByMajorName(majorName);
    }

    @Override
    public Majors getByMajorId(String majorId) {
        return majorDAO.getByMajorId(majorId);
    }

    @Override
    public List<Majors> getMajors() {
        return majorDAO.getMajors();
    }
}
