package com.example.demo.dao.impl;

import com.example.demo.dao.StaffsDAO;
import com.example.demo.entity.Classes;
import com.example.demo.entity.Majors;
import com.example.demo.entity.Persons;
import com.example.demo.entity.Staffs;
import com.example.demo.service.PersonsService;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return entityManager.createQuery(
                        "SELECT s FROM Staffs s WHERE s.email = :username or s.id= :username",
                        Staffs.class)
                .setParameter("username", authentication.getName())
                .setMaxResults(1)
                .getSingleResult();
    }
    @Override
    public List<Classes> getClasses() {
        return List.of();
    }
}