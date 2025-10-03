package com.example.demo.admin.service;

import com.example.demo.admin.dao.AdminsDAO;
import com.example.demo.admin.model.Admins;
import com.example.demo.campus.model.Campuses;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class AdminsServiceImpl implements AdminsService {
    @Override
    public Map<String, String> validateAdmin(Admins admin, MultipartFile avatarFile) {
        return adminsDAO.validateAdmin(admin, avatarFile);
    }

    @Override
    public void editAdmin(Admins admin, MultipartFile avatarFile) throws IOException {
        adminsDAO.editAdmin(admin, avatarFile);
    }

    @Override
    public Campuses getAdminCampus() {
        return adminsDAO.getAdminCampus();
    }

    private final AdminsDAO  adminsDAO;

    public AdminsServiceImpl(AdminsDAO adminsDAO) {
        this.adminsDAO = adminsDAO;
    }

    @Override
    public Admins getAdminByName(String name) {
        return adminsDAO.getAdminByName(name);
    }

    @Override
    public Admins getAdminById(String id) {
        return adminsDAO.getAdminById(id);
    }

    @Override
    public Admins getAdmin() {
        return adminsDAO.getAdmin();
    }

    @Override
    public List<Admins> getAdmins() {
        return adminsDAO.getAdmins();
    }
}
