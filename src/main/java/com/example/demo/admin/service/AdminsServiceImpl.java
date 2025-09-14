package com.example.demo.admin.service;

import com.example.demo.admin.dao.AdminsDAO;
import com.example.demo.admin.model.Admins;
import com.example.demo.campus.model.Campuses;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminsServiceImpl implements AdminsService {
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
