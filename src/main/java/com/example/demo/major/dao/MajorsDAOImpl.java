package com.example.demo.major.dao;

import com.example.demo.major.model.Majors;
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
