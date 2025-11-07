package com.example.demo.user.staff.service;

import com.example.demo.campus.model.Campuses;
import com.example.demo.classes.majorClasses.model.MajorClasses;
import com.example.demo.major.model.Majors;
import com.example.demo.user.staff.dao.StaffsDAO;
import com.example.demo.user.staff.model.Staffs;

import jakarta.mail.MessagingException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class StaffsServiceImpl implements StaffsService {
    @Override
    public List<Staffs> colleagueBycampusId(String campusId) {
        return staffsDAO.colleagueBycampusId(campusId);
    }

    @Override
    public Campuses getCampusOfStaff() {
        return staffsDAO.getCampusOfStaff();
    }

    @Override
    public List<Staffs> searchStaffsByCampus(String campusId, String searchType, String keyword, int firstResult, int pageSize) {
        return staffsDAO.searchStaffsByCampus(campusId, searchType, keyword, firstResult, pageSize);
    }

    @Override
    public long countSearchResultsByCampus(String campusId, String searchType, String keyword) {
        return staffsDAO.countSearchResultsByCampus(campusId, searchType, keyword);
    }

    @Override
    public Map<String, String> validateStaff(Staffs staff, MultipartFile avatarFile, String majorId, String campusId) {
        return staffsDAO.validateStaff(staff, avatarFile, majorId, campusId);
    }

    @Override
    public long countSearchResults(String searchType, String keyword) {
        return staffsDAO.countSearchResults(searchType, keyword);
    }

    @Override
    public List<Staffs> searchStaffs(String searchType, String keyword, int firstResult, int pageSize) {
        return staffsDAO.searchStaffs(searchType, keyword, firstResult, pageSize);
    }

    @Override
    public void editStaff(Staffs staff, MultipartFile avatarFile) throws IOException, MessagingException {
        staffsDAO.editStaff(staff, avatarFile);
    }

    @Override
    public String generateUniqueStaffId(String majorId, LocalDate createdDate) {
        return staffsDAO.generateUniqueStaffId(majorId, createdDate);
    }

    @Override
    public String generateRandomPassword(int length) {
        return staffsDAO.generateRandomPassword(length);
    }

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
