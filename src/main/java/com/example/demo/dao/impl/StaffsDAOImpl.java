package com.example.demo.dao.impl;

import com.example.demo.dao.StaffsDAO;
import com.example.demo.entity.MajorClasses;
import com.example.demo.entity.Majors;
import com.example.demo.entity.Staffs;
import com.example.demo.security.CustomUserPrincipal;
import com.example.demo.service.PersonsService;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class StaffsDAOImpl implements StaffsDAO {
    private final PersonsService personsService;

    @PersistenceContext
    private EntityManager entityManager;

    public StaffsDAOImpl(PersonsService personsService) {
        this.personsService = personsService;
    }

    @Override
    public Majors getStaffMajor() {

        return getStaff().getMajorManagement();
    }

    @Override
    public Staffs getStaff() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof CustomUserPrincipal principal)) {
            throw new IllegalStateException("No authenticated principal");
        }
        return principal.getStaff();
    }
    @Override
    public List<MajorClasses> getClasses() {
        return List.of();
    }
}