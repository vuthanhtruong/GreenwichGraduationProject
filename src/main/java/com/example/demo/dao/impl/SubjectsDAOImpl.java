package com.example.demo.dao.impl;

import com.example.demo.dao.SubjectsDAO;
import com.example.demo.entity.Majors;
import com.example.demo.entity.Subjects;
import com.example.demo.service.StaffsService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import javax.security.auth.Subject;
import java.util.List;

@Repository
@Transactional
public class SubjectsDAOImpl implements SubjectsDAO {
    @Override
    public Subjects checkNameSubject(Subjects subject) {
        return entityManager.createQuery("SELECT s FROM Subjects s WHERE s.subjectName = :name", Subjects.class).
                setParameter("name",subject.getSubjectName()).getResultList().get(0);
    }

    private StaffsService staffsService;

    public SubjectsDAOImpl(StaffsService staffsService) {
        this.staffsService = staffsService;
    }

    @Override
    public Subjects getSubjectById(String subjectId) {
        return entityManager.find(Subjects.class, subjectId);
    }

    @Override
    public void addSubject(Subjects subject) {
        subject.setCreator(staffsService.getStaffs());
        subject.setMajor(staffsService.getMajors());
        entityManager.persist(subject);
    }

    @Override
    public Subject getSubjectBySubjectId(String subjectId) {
        return entityManager.find(Subject.class, subjectId);
    }


    @Override
    public List<Subject> subjectsByMajor(Majors major) {
        List<Subject> subjects = entityManager.createQuery("SELECT s FROM Subjects s WHERE s.major = :major", Subject.class)
                .setParameter("major", major)
                .getResultList();
        return subjects;
    }

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Subject> getSubjects() {
        return List.of();
    }
}
