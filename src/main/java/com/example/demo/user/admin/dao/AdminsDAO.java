package com.example.demo.user.admin.dao;

import com.example.demo.user.admin.model.Admins;
import com.example.demo.campus.model.Campuses;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface AdminsDAO {
    Admins getAdminByName(String name);
    Admins getAdminById(String id);
    Admins getAdmin();
    List<Admins> getAdmins();
    Campuses getAdminCampus();
    void editAdmin(Admins admin, MultipartFile avatarFile) throws IOException;
    Map<String, String> validateAdmin(Admins admin, MultipartFile avatarFile);
}
