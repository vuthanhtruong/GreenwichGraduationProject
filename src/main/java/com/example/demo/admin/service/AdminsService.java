package com.example.demo.admin.service;

import com.example.demo.admin.model.Admins;

import java.util.List;

public interface AdminsService {
    Admins getAdminByName(String name);
    Admins getAdminById(String id);
    Admins getAdmin();
    List<Admins> getAdmins();
}
