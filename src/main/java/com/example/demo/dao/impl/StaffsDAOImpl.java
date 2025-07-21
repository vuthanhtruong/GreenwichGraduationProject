package com.example.demo.dao.impl;

import com.example.demo.dao.StaffsDAO;
import com.example.demo.entity.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional
public class StaffsDAOImpl implements StaffsDAO {
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
    public long numberOfStudents() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Staffs staff = entityManager.find(Staffs.class, username);

        Long totalStudents = (Long) entityManager.createQuery(
                        "SELECT COUNT(s) FROM Students s WHERE s.creator.id = :staffId")
                .setParameter("staffId", staff.getId())
                .getSingleResult();
        return totalStudents;
    }

    @Override
    public long numberOfLecturers() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Staffs staff = entityManager.find(Staffs.class, username);

        Long totalLecturers = (Long) entityManager.createQuery(
                        "SELECT COUNT(s) FROM Lecturers s WHERE s.creator.id = :staffId")
                .setParameter("staffId", staff.getId())
                .getSingleResult();
        return totalLecturers;
    }

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Staffs getStaffs() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        System.out.println("Username: " + username);

        try {
            Staffs staff = entityManager.createQuery(
                            "SELECT s FROM Staffs s WHERE s.id = :id", Staffs.class)
                    .setParameter("id", username)
                    .getSingleResult();
            return staff;
        } catch (NoResultException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Staff not found for username: " + username);
        }
    }
    @Override
    public Lecturers addLecturers(Lecturers lecturers) {
        return entityManager.merge(lecturers);
    }

    @Override
    public Students addStudents(Students students) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();

        Staffs staff = (Staffs) entityManager.find(Staffs.class, username);
        students.setMajor(staff.getMajorManagement());
        students.setCampus(staff.getCampus());
        return entityManager.merge(students);
    }

    @Override
    public List<Students> getAll() {
        return entityManager.createQuery(
                "SELECT s FROM Students s WHERE TYPE(s) = Students",
                Students.class
        ).getResultList();
    }

    @Override
    public List<Classes> getClasses() {
        return List.of();
    }

    @Override
    public List<Lecturers> getLecturers() {
        List<Lecturers> lecturers=entityManager.createQuery("from Lecturers l", Lecturers.class).getResultList();
        return lecturers;
    }

}
