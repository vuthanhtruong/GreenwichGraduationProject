package com.example.demo.deputyStaff.dao;


import com.example.demo.deputyStaff.model.DeputyStaffs;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface DeputyStaffsDAO {
    List<DeputyStaffs> getDeputyStaffs();
    List<DeputyStaffs> getPaginatedDeputyStaffs(int firstResult, int pageSize);
    long numberOfDeputyStaffs();
    void addDeputyStaff(DeputyStaffs deputyStaff, String randomPassword);
    void deleteDeputyStaff(String id);
    DeputyStaffs getDeputyStaffById(String id);
    void editDeputyStaff(DeputyStaffs deputyStaff, MultipartFile avatarFile) throws IOException;
    String generateRandomPassword(int length);
    String generateUniqueDeputyStaffId(String majorId, java.time.LocalDate createdDate);
    List<String> validateDeputyStaff(DeputyStaffs deputyStaff, MultipartFile avatarFile, String majorId, String campusId);
    List<DeputyStaffs> searchDeputyStaffs(String searchType, String keyword, int firstResult, int pageSize);
    long countSearchResults(String searchType, String keyword);

}
