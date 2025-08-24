package com.example.demo.Staff.dao;

import com.example.demo.classes.model.MajorClasses;
import com.example.demo.major.model.Majors;
import com.example.demo.Staff.model.Staffs;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public interface StaffsDAO {
    Staffs getStaff();
    Majors getStaffMajor();
    List<MajorClasses> getClasses();
    void addStaff(Staffs staff, String randomPassword);
    List<Staffs> getStaffs();
    void updateStaff(Staffs staff);
    List<String> validateStaff(Staffs staff, MultipartFile avatarFile);
    List<Staffs> getPaginatedStaffs(int firstResult, int pageSize);
    void deleteStaff(String id);
    Staffs getStaffById(String id);
    long numberOfStaffs(); // Thêm phương thức này
    String generateUniqueStaffId(String majorId, LocalDate createdDate);
    String generateRandomPassword(int length);
}