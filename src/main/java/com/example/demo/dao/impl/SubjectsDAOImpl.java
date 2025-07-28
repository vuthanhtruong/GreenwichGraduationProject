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

    private StaffsService staffsService;
    private SyllabusesService syllabusesService;
    private ClassesService classesService;

    public SubjectsDAOImpl(StaffsService staffsService, SyllabusesService syllabusesService, ClassesService classesService) {
        this.staffsService = staffsService;
        this.syllabusesService = syllabusesService;
        this.classesService = classesService;
    }

    @Override
    public void addSubject(Subjects subject) {
        subject.setCreator(staffsService.getStaffs());
        subject.setMajor(staffsService.getMajors());
        entityManager.persist(subject);
    }

    @Override
    public Subjects getSubjectById(String subjectId) {
        return entityManager.find(Subjects.class, subjectId);
    }

    @Override
    public Subjects getSubjectByName(String subjectName) {
        List<Subjects> subjects = entityManager.createQuery(
                        "SELECT s FROM Subjects s WHERE s.subjectName = :name", Subjects.class)
                .setParameter("name", subjectName)
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
        if (subject == null) {
            throw new IllegalArgumentException("Subject object cannot be null");
        }

        Subjects existingSubject = entityManager.find(Subjects.class, id);
        if (existingSubject == null) {
            throw new IllegalArgumentException("Subject with ID " + id + " not found");
        }

        // Validate required fields
        if (subject.getSubjectName() == null) {
            throw new IllegalArgumentException("Subject name cannot be null");
        }

        // Update fields
        existingSubject.setSubjectName(subject.getSubjectName());
        existingSubject.setTuition(subject.getTuition());
        existingSubject.setSemester(subject.getSemester());

        return entityManager.merge(existingSubject);
    }

    @Override
    public void deleteSubject(String id) {
        Subjects subject = entityManager.find(Subjects.class, id);
        if (subject != null) {
            syllabusesService.deleteSyllabusBySubject(subject);
            classesService.SetNullWhenDeletingSubject(subject);
            entityManager.remove(subject);
        }
    }
}