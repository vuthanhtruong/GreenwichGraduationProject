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
    public boolean existsBySubjectNameExcludingId(String subjectName, String subjectId) {
        if (subjectName == null || subjectName.trim().isEmpty()) {
            return false;
        }

        String queryString = "SELECT s FROM Subjects s WHERE s.subjectName = :name AND s.subjectId != :subjectId";
        try {
            List<Subjects> subjects = entityManager.createQuery(queryString, Subjects.class)
                    .setParameter("name", subjectName.trim())
                    .setParameter("subjectId", subjectId != null ? subjectId : "")
                    .getResultList();
            return !subjects.isEmpty();
        } catch (Exception e) {
            throw new RuntimeException("Error checking subject existence: " + e.getMessage(), e);
        }
    }
    @Override
    public List<Subjects> YetAcceptedSubjects() {
        return entityManager.createQuery(
                        "SELECT s FROM Subjects s WHERE s.acceptor is null ORDER BY s.semester ASC",
                        Subjects.class)
                .getResultList();
    }

    @Override
    public boolean existsSubjectById(String subjectId) {
        return entityManager.find(Subjects.class, subjectId) != null;
    }

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Subjects getSubjectById(String id) {
        if (id == null) {
            return null;
        }
        try {
            return entityManager.find(Subjects.class, id);
        } catch (IllegalArgumentException e) {
            return null; // Handle invalid ID gracefully
        }
    }

    @Override
    public List<Subjects> getSubjects() {
        return entityManager.createQuery("SELECT s FROM Subjects s where s.acceptor is not null ", Subjects.class)
                .getResultList()
                .stream()
                .sorted(Comparator.comparing(
                                this::getMajorName,
                                Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(Subjects::getSemester, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(Subjects::getSubjectId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Subjects> getPaginatedSubjects(int firstResult, int pageSize) {
        return entityManager.createQuery(
                        "SELECT s FROM Subjects s LEFT JOIN FETCH s.acceptor",
                        Subjects.class)
                .setFirstResult(firstResult)
                .setMaxResults(pageSize)
                .getResultList();
    }

    @Override
    public long numberOfSubjects() {
        return entityManager.createQuery(
                        "SELECT COUNT(s) FROM Subjects s",
                        Long.class)
                .getSingleResult();
    }

    @Override
    public List<Subjects> searchSubjects(String searchType, String keyword, int firstResult, int pageSize) {
        if (keyword == null || keyword.trim().isEmpty() || searchType == null) {
            return List.of();
        }
        String queryString ="";
        if ("name".equals(searchType)) {
            queryString += " SELECT s FROM Subjects s WHERE LOWER(s.subjectName) LIKE LOWER(:keyword)";
        } else if ("id".equals(searchType)) {
            queryString += " SELECT s FROM Subjects s WHERE s.subjectId LIKE :keyword";
        } else {
            return List.of();
        }
        return entityManager.createQuery(queryString, Subjects.class)
                .setParameter("keyword", "%" + keyword.trim() + "%")
                .setFirstResult(firstResult)
                .setMaxResults(pageSize)
                .getResultList();
    }

    @Override
    public long countSearchResults(String searchType, String keyword) {
        if (keyword == null || keyword.trim().isEmpty() || searchType == null) {
            return 0;
        }
        String queryString ="";
        if ("name".equals(searchType)) {
            queryString += " SELECT COUNT(s) FROM Subjects s WHERE LOWER(s.subjectName) LIKE LOWER(:keyword)";
        } else if ("id".equals(searchType)) {
            queryString += " SELECT COUNT(s) FROM Subjects s WHERE s.subjectId LIKE :keyword";
        } else {
            return 0;
        }
        return entityManager.createQuery(queryString, Long.class)
                .setParameter("keyword", "%" + keyword.trim() + "%")
                .getSingleResult();
    }

    @Override
    public List<Subjects> getSubjectsByAdmissionYear(Integer admissionYear) {
        if (admissionYear == null) {
            return List.of();
        }
        return entityManager.createQuery(
                        "SELECT s FROM Subjects s LEFT JOIN TuitionByYear t ON s.subjectId = t.id.subjectId WHERE t.id.admissionYear = :admissionYear ORDER BY s.subjectName ASC",
                        Subjects.class)
                .setParameter("admissionYear", admissionYear)
                .getResultList();
    }

    private String getMajorName(Subjects subject) {
        if (subject instanceof MajorSubjects majorSubject && majorSubject.getMajor() != null) {
            return majorSubject.getMajor().getMajorName();
        }
        return null;
    }
    @Override
    public void approveSubjects(List<String> subjectIds, String acceptorId) {
        if (subjectIds == null || subjectIds.isEmpty() || acceptorId == null) {
            return;
        }
        entityManager.createQuery(
                        "UPDATE Subjects s SET s.acceptor.id = :acceptorId WHERE s.subjectId IN :subjectIds AND s.acceptor is null")
                .setParameter("acceptorId", acceptorId)
                .setParameter("subjectIds", subjectIds)
                .executeUpdate();
    }

}