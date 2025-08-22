package com.example.demo.subject.dao;

import com.example.demo.subject.model.Subjects;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class SubjectsDAOImpl implements SubjectsDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Subjects> getSubjects() {
        return entityManager.createQuery(
                        "SELECT s FROM Subjects s ORDER BY s.subjectName ASC",
                        Subjects.class)
                .getResultList();
    }
}