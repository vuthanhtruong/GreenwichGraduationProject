package com.example.demo.service.impl;

import com.example.demo.dao.StaffsDAO;
import com.example.demo.entity.*;
import com.example.demo.service.StaffsService;

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
