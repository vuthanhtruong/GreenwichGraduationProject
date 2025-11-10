// File: MinorSyllabusesDAOImpl.java
package com.example.demo.syllabus.minorSyllabuses.dao;

import com.example.demo.subject.minorSubject.model.MinorSubjects;
import com.example.demo.syllabus.minorSyllabuses.model.MinorSyllabuses;
import com.example.demo.user.staff.service.StaffsService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class MinorSyllabusesDAOImpl implements MinorSyllabusesDAO {

    @PersistenceContext
    private EntityManager entityManager;

    public MinorSyllabusesDAOImpl() {
    }

    @Override
    public void addSyllabus(MinorSyllabuses syllabus) {
        entityManager.persist(syllabus);
    }

    @Override
    public MinorSyllabuses getSyllabusById(String syllabusId) {
        return entityManager.find(MinorSyllabuses.class, syllabusId);
    }

    @Override
    public List<MinorSyllabuses> getSyllabusesBySubject(MinorSubjects subject) {
        return entityManager.createQuery(
                        "SELECT s FROM MinorSyllabuses s WHERE s.subject = :subject", MinorSyllabuses.class)
                .setParameter("subject", subject)
                .getResultList();
    }

    @Override
    public void deleteSyllabusBySubject(MinorSubjects subject) {
        List<MinorSyllabuses> list = getSyllabusesBySubject(subject);
        for (MinorSyllabuses s : list) {
            entityManager.remove(s);
        }
    }

    @Override
    public List<MinorSyllabuses> getPaginatedSyllabuses(String subjectId, int firstResult, int pageSize) {
        return entityManager.createQuery(
                        "SELECT s FROM MinorSyllabuses s WHERE s.subject.subjectId = :subjectId", MinorSyllabuses.class)
                .setParameter("subjectId", subjectId)
                .setFirstResult(firstResult)
                .setMaxResults(pageSize)
                .getResultList();
    }

    @Override
    public Long numberOfSyllabuses(String subjectId) {
        return entityManager.createQuery(
                        "SELECT COUNT(s) FROM MinorSyllabuses s WHERE s.subject.subjectId = :subjectId", Long.class)
                .setParameter("subjectId", subjectId)
                .getSingleResult();
    }

    @Override
    public void deleteSyllabus(MinorSyllabuses syllabus) {
        if (syllabus != null) {
            if (!entityManager.contains(syllabus)) {
                syllabus = entityManager.merge(syllabus);
            }
            entityManager.remove(syllabus);
        }
    }
}