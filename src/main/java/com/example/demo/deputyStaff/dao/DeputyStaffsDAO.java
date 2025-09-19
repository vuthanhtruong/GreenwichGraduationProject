package com.example.demo.deputyStaff.dao;


import com.example.demo.deputyStaff.model.DeputyStaffs;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface DeputyStaffsDAO {
    List<DeputyStaffs> getDeputyStaffs();
    List<DeputyStaffs> getPaginatedDeputyStaffs(int firstResult, int pageSize);
    long numberOfDeputyStaffs();
    void addDeputyStaff(DeputyStaffs deputyStaff, String randomPassword);
    void deleteDeputyStaff(String id);
    DeputyStaffs getDeputyStaffById(String id);
    void editDeputyStaff(DeputyStaffs deputyStaff, MultipartFile avatarFile) throws IOException;
    String generateRandomPassword(int length);
    String generateUniqueDeputyStaffId(LocalDate createdDate);
    Map<String, String> validateDeputyStaff(DeputyStaffs deputyStaff, MultipartFile avatarFile, String campusId);
    List<DeputyStaffs> searchDeputyStaffs(String searchType, String keyword, int firstResult, int pageSize);
    long countSearchResults(String searchType, String keyword);
}
