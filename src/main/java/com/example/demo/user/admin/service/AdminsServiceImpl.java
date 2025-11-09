package com.example.demo.user.admin.service;

import com.example.demo.user.admin.dao.AdminsDAO;
import com.example.demo.user.admin.model.Admins;
import com.example.demo.campus.model.Campuses;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class AdminsServiceImpl implements AdminsService {
    @Override
    public List<Admins> yourManagerByCampusId(String campusId) {
        return adminsDAO.yourManagerByCampusId(campusId);
    }

    @Override
    public List<Admins> getPaginatedAdmins(int firstResult, int pageSize) {
        return adminsDAO.getPaginatedAdmins(firstResult, pageSize);
    }

    @Override
    public long countAdmins() {
        return adminsDAO.countAdmins();
    }

    @Override
    public List<Admins> searchAdmins(String searchType, String keyword, int firstResult, int pageSize) {
        return adminsDAO.searchAdmins(searchType, keyword, firstResult, pageSize);
    }

    @Override
    public long countSearchResults(String searchType, String keyword) {
        return adminsDAO.countSearchResults(searchType, keyword);
    }

    @Override
    public String generateAdminId(LocalDate date) {
        return adminsDAO.generateAdminId(date);
    }

    @Override
    public String generateRandomPassword(int length) {
        return adminsDAO.generateRandomPassword(length);
    }

    @Override
    public void addAdmin(Admins admin, String rawPassword) {
        adminsDAO.addAdmin(admin, rawPassword);
    }

    @Override
    public void deleteAdmin(String id) {
        adminsDAO.deleteAdmin(id);
    }

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
