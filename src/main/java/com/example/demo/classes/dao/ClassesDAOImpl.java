package com.example.demo.classes.dao;

import com.example.demo.classes.model.Classes;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public class ClassesDAOImpl implements ClassesDAO{
    @Override
    public Classes findClassById(String classId) {
        return entityManager.find(Classes.class, classId);
    }

    @PersistenceContext
    private EntityManager entityManager;
}
