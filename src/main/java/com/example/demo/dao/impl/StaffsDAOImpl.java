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
    public Majors getMajors() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Staffs staff;
                staff = entityManager.createQuery(
                                "SELECT s FROM Staffs s WHERE s.email = :username or s.id= :username",
                                Staffs.class)
                        .setParameter("username", username)
                        .setMaxResults(1)
                        .getSingleResult();
        return staff.getMajorManagement();
    }

    @Override
    public Staffs getStaffs() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Staffs staff = entityManager.createQuery(
                        "SELECT s FROM Staffs s WHERE s.email = :username or s.id= :username",
                        Staffs.class)
                .setParameter("username", username)
                .setMaxResults(1)
                .getSingleResult();
        return staff;
    }

    @Override
    public List<Classes> getClasses() {
        return List.of();
    }
}