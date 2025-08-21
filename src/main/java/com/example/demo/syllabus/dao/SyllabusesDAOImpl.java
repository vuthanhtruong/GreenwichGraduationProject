package com.example.demo.syllabus.dao;

import com.example.demo.subject.model.MajorSubjects;
import com.example.demo.syllabus.model.Syllabuses;
import com.example.demo.majorStaff.service.StaffsService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
@Transactional
public class SyllabusesDAOImpl implements SyllabusesDAO {
    @Override
    public void deleteSyllabusBySubject(MajorSubjects subject) {
        List<Syllabuses> syllabusesList=entityManager.createQuery("select s from Syllabuses s where s.subject=:subject",Syllabuses.class).
                setParameter("subject",subject).getResultList();
        for (Syllabuses syllabuses : syllabusesList) {
            entityManager.remove(syllabuses);
        }
    }

    private StaffsService staffsService;

    public SyllabusesDAOImpl(StaffsService staffsService) {
        this.staffsService= staffsService;
    }

    @Override
    public Syllabuses getSyllabusById(String syllabusId) {
        return entityManager.find(Syllabuses.class, syllabusId);
    }

    @Override
    public void addSyllabus(Syllabuses syllabus) {
        syllabus.setCreator(staffsService.getStaff());
        entityManager.persist(syllabus);
    }

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Syllabuses> syllabusesList() {
        return entityManager.createQuery("from Syllabuses", Syllabuses.class).getResultList();
    }

    @Override
    public List<Syllabuses> getSyllabusesBySubject(MajorSubjects subject) {
        return entityManager.createQuery("select s FROM Syllabuses s where s.subject=:subject", Syllabuses.class).
                setParameter("subject", subject).getResultList();
    }
}
