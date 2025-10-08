package com.example.demo.syllabus.dao;

import com.example.demo.majorSubject.model.MajorSubjects;
import com.example.demo.syllabus.model.Syllabuses;
import com.example.demo.staff.service.StaffsService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class SyllabusesDAOImpl implements SyllabusesDAO {

    @PersistenceContext
    private EntityManager entityManager;

    private final StaffsService staffsService;

    public SyllabusesDAOImpl(StaffsService staffsService) {
        this.staffsService = staffsService;
    }

    @Override
    public void addSyllabus(Syllabuses syllabus) {
        syllabus.setCreator(staffsService.getStaff());
        entityManager.persist(syllabus);
    }

    @Override
    public Syllabuses getSyllabusById(String syllabusId) {
        return entityManager.find(Syllabuses.class, syllabusId);
    }

    @Override
    public List<Syllabuses> getSyllabusesBySubject(MajorSubjects subject) {
        return entityManager.createQuery("select s FROM Syllabuses s where s.subject = :subject", Syllabuses.class)
                .setParameter("subject", subject)
                .getResultList();
    }

    @Override
    public void deleteSyllabusBySubject(MajorSubjects subject) {
        List<Syllabuses> syllabusesList = entityManager.createQuery("select s from Syllabuses s where s.subject = :subject", Syllabuses.class)
                .setParameter("subject", subject)
                .getResultList();
        for (Syllabuses syllabus : syllabusesList) {
            entityManager.remove(syllabus);
        }
    }

    @Override
    public List<Syllabuses> getPaginatedSyllabuses(String subjectId, int firstResult, int pageSize) {
        return entityManager.createQuery("select s from Syllabuses s where s.subject.subjectId = :subjectId", Syllabuses.class)
                .setParameter("subjectId", subjectId)
                .setFirstResult(firstResult)
                .setMaxResults(pageSize)
                .getResultList();
    }

    @Override
    public Long numberOfSyllabuses(String subjectId) {
        return entityManager.createQuery("select count(s) from Syllabuses s where s.subject.subjectId = :subjectId", Long.class)
                .setParameter("subjectId", subjectId)
                .getSingleResult();
    }
}