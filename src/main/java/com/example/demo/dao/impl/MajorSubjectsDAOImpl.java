package com.example.demo.dao.impl;

import com.example.demo.dao.MajorSubjectsDAO;
import com.example.demo.entity.Enums.SubjectTypes;
import com.example.demo.entity.Majors;
import com.example.demo.entity.MajorSubjects;
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
public class MajorSubjectsDAOImpl implements MajorSubjectsDAO {

    @PersistenceContext
    private EntityManager entityManager;

    private final StaffsService staffsService;
    private final SyllabusesService syllabusesService;
    private final ClassesService classesService;

    public MajorSubjectsDAOImpl(StaffsService staffsService, SyllabusesService syllabusesService, ClassesService classesService) {
        if (staffsService == null || syllabusesService == null || classesService == null) {
            throw new IllegalArgumentException("Services cannot be null");
        }
        this.staffsService = staffsService;
        this.syllabusesService = syllabusesService;
        this.classesService = classesService;
    }

    @Override
    public void addSubject(MajorSubjects subject) {
        if (subject == null) {
            throw new IllegalArgumentException("Subject object cannot be null");
        }
        subject.setCreator(staffsService.getStaff());
        subject.setMajor(staffsService.getStaffMajor());
        subject.setRequirementType(SubjectTypes.TOPUP_PREPARATION);
        entityManager.persist(subject);
    }

    @Override
    public MajorSubjects getSubjectById(String subjectId) {
        if (subjectId == null) {
            throw new IllegalArgumentException("Subject ID cannot be null");
        }
        return entityManager.find(MajorSubjects.class, subjectId);
    }

    @Override
    public MajorSubjects getSubjectByName(String subjectName) {
        if (subjectName == null || subjectName.trim().isEmpty()) {
            return null;
        }
        List<MajorSubjects> subjects = entityManager.createQuery(
                        "SELECT s FROM MajorSubjects s WHERE s.subjectName = :name", MajorSubjects.class)
                .setParameter("name", subjectName.trim())
                .getResultList();
        return subjects.isEmpty() ? null : subjects.get(0);
    }

    @Override
    public MajorSubjects checkNameSubject(MajorSubjects subject) {
        if (subject == null || subject.getSubjectName() == null || subject.getSubjectName().trim().isEmpty()) {
            return null;
        }
        List<MajorSubjects> subjects = entityManager.createQuery(
                        "SELECT s FROM MajorSubjects s WHERE s.subjectName = :name", MajorSubjects.class)
                .setParameter("name", subject.getSubjectName().trim())
                .getResultList();
        return subjects.isEmpty() ? null : subjects.get(0);
    }

    @Override
    public List<MajorSubjects> subjectsByMajor(Majors major) {
        if (major == null) {
            return List.of();
        }
        return entityManager.createQuery(
                        "SELECT s FROM MajorSubjects s WHERE s.major = :major ORDER BY s.semester ASC",
                        MajorSubjects.class)
                .setParameter("major", major)
                .getResultList();
    }


    @Override
    public List<MajorSubjects> getSubjects() {
        return entityManager.createQuery("SELECT s FROM MajorSubjects s", MajorSubjects.class).getResultList();
    }

    @Override
    public MajorSubjects updateSubject(String id, MajorSubjects subject) {
        if (subject == null || id == null) {
            throw new IllegalArgumentException("Subject object or ID cannot be null");
        }

        MajorSubjects existingSubject = entityManager.find(MajorSubjects.class, id);
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
        MajorSubjects subject = entityManager.find(MajorSubjects.class, id);
        if (subject != null) {
            syllabusesService.deleteSyllabusBySubject(subject);
            classesService.SetNullWhenDeletingSubject(subject);
            entityManager.remove(subject);
        }
    }
}