package com.example.demo.majorstaff.service;

import com.example.demo.classes.model.MajorClasses;
import com.example.demo.major.model.Majors;
import com.example.demo.majorstaff.dao.StaffsDAO;
import com.example.demo.majorstaff.model.Staffs;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StaffsServiceImpl implements StaffsService {

    @Override
    public Majors getStaffMajor() {
        return staffsDAO.getStaffMajor();
    }
    private final StaffsDAO staffsDAO;

    public StaffsServiceImpl(StaffsDAO staffsDAO) {
        this.staffsDAO = staffsDAO;
    }
    @Override
    public Staffs getStaff() {
        return staffsDAO.getStaff();
    }

    @Override
    public List<MajorClasses> getClasses() {
        return staffsDAO.getClasses();
    }

}
