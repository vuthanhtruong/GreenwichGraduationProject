package com.example.demo.majorSyllabus.dao;

import com.example.demo.majorSubject.model.MajorSubjects;
import com.example.demo.majorSyllabus.model.MajorSyllabuses;
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
    public void addSyllabus(MajorSyllabuses syllabus) {
        syllabus.setCreator(staffsService.getStaff());
        entityManager.persist(syllabus);
    }

    @Override
    public MajorSyllabuses getSyllabusById(String syllabusId) {
        return entityManager.find(MajorSyllabuses.class, syllabusId);
    }

    @Override
    public List<MajorSyllabuses> getSyllabusesBySubject(MajorSubjects subject) {
        return entityManager.createQuery("select s FROM MajorSyllabuses s where s.subject = :subject", MajorSyllabuses.class)
                .setParameter("subject", subject)
                .getResultList();
    }

    @Override
    public void deleteSyllabusBySubject(MajorSubjects subject) {
        List<MajorSyllabuses> syllabusesList = entityManager.createQuery("select s from MajorSyllabuses s where s.subject = :subject", MajorSyllabuses.class)
                .setParameter("subject", subject)
                .getResultList();
        for (MajorSyllabuses syllabus : syllabusesList) {
            entityManager.remove(syllabus);
        }
    }

    @Override
    public List<MajorSyllabuses> getPaginatedSyllabuses(String subjectId, int firstResult, int pageSize) {
        return entityManager.createQuery("select s from MajorSyllabuses s where s.subject.subjectId = :subjectId", MajorSyllabuses.class)
                .setParameter("subjectId", subjectId)
                .setFirstResult(firstResult)
                .setMaxResults(pageSize)
                .getResultList();
    }

    @Override
    public Long numberOfSyllabuses(String subjectId) {
        return entityManager.createQuery("select count(s) from MajorSyllabuses s where s.subject.subjectId = :subjectId", Long.class)
                .setParameter("subjectId", subjectId)
                .getSingleResult();
    }
}