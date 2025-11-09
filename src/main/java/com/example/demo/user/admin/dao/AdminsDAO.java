package com.example.demo.user.admin.dao;

import com.example.demo.user.admin.model.Admins;
import com.example.demo.campus.model.Campuses;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface AdminsDAO {
    Map<String, String> validateAdmin(Admins admin, MultipartFile avatarFile);
    Admins getAdmin();
    Admins getAdminById(String id);
    Admins getAdminByName(String name);
    Campuses getAdminCampus();
    List<Admins> getAdmins();
    List<Admins> getPaginatedAdmins(int firstResult, int pageSize);
    long countAdmins();
    List<Admins> searchAdmins(String searchType, String keyword, int firstResult, int pageSize);
    long countSearchResults(String searchType, String keyword);
    String generateAdminId(LocalDate date);
    String generateRandomPassword(int length);
    void addAdmin(Admins admin, String rawPassword);
    void editAdmin(Admins admin, MultipartFile avatarFile) throws java.io.IOException;
    void deleteAdmin(String id);
    List<Admins> yourManagerByCampusId(String campusId);
}
