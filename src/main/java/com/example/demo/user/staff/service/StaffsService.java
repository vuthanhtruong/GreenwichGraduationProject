package com.example.demo.user.staff.service;



import com.example.demo.campus.model.Campuses;
import com.example.demo.classes.majorClasses.model.MajorClasses;
import com.example.demo.major.model.Majors;
import com.example.demo.user.staff.model.Staffs;
import jakarta.mail.MessagingException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface StaffsService {
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
    List<Staffs> searchStaffsByCampus(String campusId, String searchType, String keyword, int firstResult, int pageSize);
    long countSearchResultsByCampus(String campusId, String searchType, String keyword);
    Campuses getCampusOfStaff();
    List<Staffs> colleagueBycampusId(String campusId);
}
