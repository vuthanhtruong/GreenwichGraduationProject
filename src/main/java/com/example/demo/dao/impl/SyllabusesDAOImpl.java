package com.example.demo.dao.impl;

import com.example.demo.dao.SyllabusesDAO;
import com.example.demo.entity.Subjects;
import com.example.demo.entity.Syllabuses;
import com.example.demo.service.StaffsService;
import com.example.demo.service.SyllabusesService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
@Transactional
public class SyllabusesDAOImpl implements SyllabusesDAO {
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
        syllabus.setCreator(staffsService.getStaffs());
        entityManager.persist(syllabus);
    }

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Syllabuses> syllabusesList() {
        return entityManager.createQuery("from Syllabuses", Syllabuses.class).getResultList();
    }

    @Override
    public List<Syllabuses> getSyllabusesBySubject(Subjects subject) {
        return entityManager.createQuery("select s FROM Syllabuses s where s.subject=:subject", Syllabuses.class).
                setParameter("subject", subject).getResultList();
    }
}
