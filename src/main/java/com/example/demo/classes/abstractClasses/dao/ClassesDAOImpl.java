package com.example.demo.classes.abstractClasses.dao;

import com.example.demo.classes.abstractClasses.model.Classes;
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

    @Override
    public boolean existsByNameClass(String nameClass) {
        String jpql = "SELECT COUNT(c) > 0 FROM Classes c WHERE LOWER(TRIM(c.nameClass)) = LOWER(TRIM(:nameClass))";
        return entityManager.createQuery(jpql, Boolean.class)
                .setParameter("nameClass", nameClass)
                .getSingleResult();
    }

    @Override
    public boolean existsByNameClassExcludingId(String nameClass, String excludeClassId) {
        String jpql = """
                SELECT COUNT(c) > 0 FROM Classes c 
                WHERE LOWER(TRIM(c.nameClass)) = LOWER(TRIM(:nameClass))
                  AND c.classId <> :excludeId
                """;
        return entityManager.createQuery(jpql, Boolean.class)
                .setParameter("nameClass", nameClass)
                .setParameter("excludeId", excludeClassId)
                .getSingleResult();
    }

    @PersistenceContext
    private EntityManager entityManager;
}
