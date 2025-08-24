package com.example.demo.Staff.service;

import com.example.demo.classes.model.MajorClasses;
import com.example.demo.major.model.Majors;
import com.example.demo.Staff.dao.StaffsDAO;
import com.example.demo.Staff.model.Staffs;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class StaffsServiceImpl implements StaffsService {
    @Override
    public long numberOfStaffs() {
        return staffsDAO.numberOfStaffs();
    }

    @Override
    public void addStaff(Staffs staff, String randomPassword) {
        staffsDAO.addStaff(staff, randomPassword);
    }

    @Override
    public List<Staffs> getStaffs() {
        return staffsDAO.getStaffs();
    }

    @Override
    public void updateStaff(Staffs staff) {
        staffsDAO.updateStaff(staff);
    }

    @Override
    public List<String> validateStaff(Staffs staff, MultipartFile avatarFile) {
        return staffsDAO.validateStaff(staff, avatarFile);
    }

    @Override
    public List<Staffs> getPaginatedStaffs(int firstResult, int pageSize) {
        return staffsDAO.getPaginatedStaffs(firstResult, pageSize);
    }

    @Override
    public void deleteStaff(String id) {
        staffsDAO.deleteStaff(id);
    }

    @Override
    public Staffs getStaffById(String id) {
        return staffsDAO.getStaffById(id);
    }

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
