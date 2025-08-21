package com.example.demo.majorStaff.dao;

import com.example.demo.classes.model.MajorClasses;
import com.example.demo.major.model.Majors;
import com.example.demo.majorStaff.model.Staffs;
import com.example.demo.security.model.CustomUserPrincipal;
import com.example.demo.person.service.PersonsService;
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
        return (Staffs) principal.getPerson();
    }
    @Override
    public List<MajorClasses> getClasses() {
        return List.of();
    }
}