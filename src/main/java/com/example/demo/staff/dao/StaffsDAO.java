package com.example.demo.staff.dao;

import com.example.demo.classes.model.MajorClasses;
import com.example.demo.major.model.Majors;
import com.example.demo.staff.model.Staffs;
import jakarta.mail.MessagingException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface StaffsDAO {
    Staffs getStaff();
    Majors getStaffMajor();
    List<MajorClasses> getClasses();
    void addStaff(Staffs staff, String randomPassword);
    List<Staffs> getStaffs();
    void editStaff(Staffs staff, MultipartFile avatarFile) throws IOException, MessagingException;
    Map<String, String> validateStaff(Staffs staff, MultipartFile avatarFile, String majorId, String campusId);
    List<Staffs> getPaginatedStaffs(int firstResult, int pageSize);
    void deleteStaff(String id);
    Staffs getStaffById(String id);
    long numberOfStaffs(); // Thêm phương thức này
    String generateUniqueStaffId(String majorId, LocalDate createdDate);
    String generateRandomPassword(int length);
    List<Staffs> searchStaffs(String searchType, String keyword, int firstResult, int pageSize);
    long countSearchResults(String searchType, String keyword);
}