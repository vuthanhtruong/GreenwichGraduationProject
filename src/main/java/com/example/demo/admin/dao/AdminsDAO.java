package com.example.demo.admin.dao;

import com.example.demo.admin.model.Admins;

import java.util.List;

public interface AdminsDAO {
    Admins getAdminByName(String name);
    Admins getAdminById(String id);
    Admins getAdmin();
    List<Admins> getAdmins();
}
