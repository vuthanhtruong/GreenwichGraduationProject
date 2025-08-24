package com.example.demo.major.dao;

import com.example.demo.major.model.Majors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
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
        return entityManager.createQuery("from Majors", Majors.class).getResultList();
    }
}
