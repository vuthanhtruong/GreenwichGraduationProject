package com.example.demo.subject.dao;

import com.example.demo.subject.model.MajorSubjects;
import com.example.demo.subject.model.Subjects;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@Transactional
public class SubjectsDAOImpl implements SubjectsDAO {
    @Override
    public Subjects getSubjectById(String id) {
        return entityManager.find(Subjects.class, id);
    }

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Subjects> getSubjects() {
        return entityManager.createQuery("SELECT s FROM Subjects s", Subjects.class)
                .getResultList()
                .stream()
                .sorted(Comparator.comparing(
                                this::getMajorName,
                                Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(Subjects::getSemester, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(Subjects::getSubjectId))
                .collect(Collectors.toList());
    }

    private String getMajorName(Subjects subject) {
        if (subject instanceof MajorSubjects majorSubject && majorSubject.getMajor() != null) {
            return majorSubject.getMajor().getMajorName();
        }
        return null;
    }

    @Override
    public List<Subjects> getSubjectsByAdmissionYear(Integer admissionYear) {
        return entityManager.createQuery(
                        "SELECT s FROM Subjects s JOIN TuitionByYear t ON s.subjectId = t.id.subjectId WHERE t.id.admissionYear = :admissionYear ORDER BY s.subjectName ASC",
                        Subjects.class)
                .setParameter("admissionYear", admissionYear)
                .getResultList();
    }
}