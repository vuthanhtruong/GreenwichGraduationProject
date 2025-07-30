package com.example.demo.dao.impl;

import com.example.demo.dao.SubjectsDAO;
import com.example.demo.entity.Majors;
import com.example.demo.entity.Subjects;
import com.example.demo.service.ClassesService;
import com.example.demo.service.StaffsService;
import com.example.demo.service.SyllabusesService;
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

    private final StaffsService staffsService;
    private final SyllabusesService syllabusesService;
    private final ClassesService classesService;

    public SubjectsDAOImpl(StaffsService staffsService, SyllabusesService syllabusesService, ClassesService classesService) {
        if (staffsService == null || syllabusesService == null || classesService == null) {
            throw new IllegalArgumentException("Services cannot be null");
        }
        this.staffsService = staffsService;
        this.syllabusesService = syllabusesService;
        this.classesService = classesService;
    }

    @Override
    public void addSubject(Subjects subject) {
        if (subject == null) {
            throw new IllegalArgumentException("Subject object cannot be null");
        }
        subject.setCreator(staffsService.getStaffs());
        subject.setMajor(staffsService.getMajors());
        entityManager.persist(subject);
    }

    @Override
    public Subjects getSubjectById(String subjectId) {
        if (subjectId == null) {
            throw new IllegalArgumentException("Subject ID cannot be null");
        }
        return entityManager.find(Subjects.class, subjectId);
    }

    @Override
    public Subjects getSubjectByName(String subjectName) {
        if (subjectName == null || subjectName.trim().isEmpty()) {
            return null;
        }
        List<Subjects> subjects = entityManager.createQuery(
                        "SELECT s FROM Subjects s WHERE s.subjectName = :name", Subjects.class)
                .setParameter("name", subjectName.trim())
                .getResultList();
        return subjects.isEmpty() ? null : subjects.get(0);
    }

    @Override
    public Subjects checkNameSubject(Subjects subject) {
        if (subject == null || subject.getSubjectName() == null || subject.getSubjectName().trim().isEmpty()) {
            return null;
        }
        List<Subjects> subjects = entityManager.createQuery(
                        "SELECT s FROM Subjects s WHERE s.subjectName = :name", Subjects.class)
                .setParameter("name", subject.getSubjectName().trim())
                .getResultList();
        return subjects.isEmpty() ? null : subjects.get(0);
    }

    @Override
    public List<Subjects> subjectsByMajor(Majors major) {
        if (major == null) {
            return List.of();
        }
        return entityManager.createQuery(
                        "SELECT s FROM Subjects s WHERE s.major = :major", Subjects.class)
                .setParameter("major", major)
                .getResultList();
    }

    @Override
    public List<Subjects> getSubjects() {
        return entityManager.createQuery("SELECT s FROM Subjects s", Subjects.class).getResultList();
    }

    @Override
    public Subjects updateSubject(String id, Subjects subject) {
        if (subject == null || id == null) {
            throw new IllegalArgumentException("Subject object or ID cannot be null");
        }

        Subjects existingSubject = entityManager.find(Subjects.class, id);
        if (existingSubject == null) {
            throw new IllegalArgumentException("Subject with ID " + id + " not found");
        }

        if (subject.getSubjectName() == null) {
            throw new IllegalArgumentException("Subject name cannot be null");
        }

        existingSubject.setSubjectName(subject.getSubjectName());
        if (subject.getTuition() != null) existingSubject.setTuition(subject.getTuition());
        if (subject.getSemester() != null) existingSubject.setSemester(subject.getSemester());

        return entityManager.merge(existingSubject);
    }

    @Override
    public void deleteSubject(String id) {
        if (id == null) {
            throw new IllegalArgumentException("Subject ID cannot be null");
        }
        Subjects subject = entityManager.find(Subjects.class, id);
        if (subject != null) {
            syllabusesService.deleteSyllabusBySubject(subject);
            classesService.SetNullWhenDeletingSubject(subject);
            entityManager.remove(subject);
        }
    }
}