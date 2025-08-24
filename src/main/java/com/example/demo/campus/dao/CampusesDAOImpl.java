package com.example.demo.campus.dao;

import com.example.demo.campus.model.Campuses;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class CampusesDAOImpl implements CampusesDAO {
    @Override
    public Campuses getCampusById(String campusId) {
        return entityManager.find(Campuses.class, campusId);
    }

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Campuses> getCampuses() {
        return entityManager.createQuery("from Campuses", Campuses.class).getResultList();
    }
}
