package com.example.demo.dao.impl;

import com.example.demo.dao.MajorDAO;
import com.example.demo.entity.Majors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

public class MajorsDAOImpl implements MajorDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Majors getByMajorName(String majorName) {
        return null;
    }

    @Override
    public Majors getByMajorId(String majorId) {
        return entityManager.find(Majors.class, majorId);
    }

    @Override
    public List<Majors> getMajors() {
        return List.of();
    }
}
