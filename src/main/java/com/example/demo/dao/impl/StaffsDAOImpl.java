package com.example.demo.dao.impl;

import com.example.demo.dao.StaffsDAO;
import com.example.demo.entity.Classes;
import com.example.demo.entity.Majors;
import com.example.demo.entity.Persons;
import com.example.demo.entity.Staffs;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class StaffsDAOImpl implements StaffsDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public boolean existsByPhoneNumberExcludingId(String phoneNumber, String id) {
        Query query = entityManager.createQuery(
                "SELECT COUNT(s) > 0 FROM Persons s WHERE s.phoneNumber = :phoneNumber AND s.id != :id"
        );
        query.setParameter("phoneNumber", phoneNumber);
        query.setParameter("id", id);
        return (boolean) query.getSingleResult();
    }

    @Override
    public boolean existsByEmailExcludingId(String email, String id) {
        Query query = entityManager.createQuery(
                "SELECT COUNT(s) > 0 FROM Persons s WHERE s.email = :email AND s.id != :id"
        );
        query.setParameter("email", email);
        query.setParameter("id", id);
        return (boolean) query.getSingleResult();
    }

    @Override
    public boolean existsPersonById(String id) {
        Query query = entityManager.createQuery(
                "SELECT COUNT(p) FROM Persons p WHERE p.id = :id");
        query.setParameter("id", id);
        return (Long) query.getSingleResult() > 0;
    }

    @Override
    public boolean existsByEmail(String email) {
        Query query = entityManager.createQuery(
                "SELECT COUNT(p) FROM Persons p WHERE p.email = :email");
        query.setParameter("email", email);
        return (Long) query.getSingleResult() > 0;
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        Query query = entityManager.createQuery(
                "SELECT COUNT(p) FROM Persons p WHERE p.phoneNumber = :phoneNumber");
        query.setParameter("phoneNumber", phoneNumber);
        return (Long) query.getSingleResult() > 0;
    }

    @Override
    public Majors getMajors() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Staffs staff = entityManager.find(Staffs.class, username);
        return staff.getMajorManagement();
    }

    @Override
    public Staffs getStaffs() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("No authenticated user found.");
        }

        Object principal = authentication.getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        System.out.println("Username: " + username);

        Persons user = entityManager.find(Persons.class, username);
        if (user == null) {
            throw new IllegalArgumentException("User not found with username: " + username);
        }
        if (!(user instanceof Staffs)) {
            throw new SecurityException("User is not a staff member. Entity type: " + user.getClass().getSimpleName());
        }
        return (Staffs) user;
    }

    @Override
    public List<Classes> getClasses() {
        return List.of();
    }
}