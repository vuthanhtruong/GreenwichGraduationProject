package com.example.demo.retakeSubjects.dao;

import com.example.demo.retakeSubjects.model.TemporaryRetakeSubjects;
import com.example.demo.studentRequiredMajorSubjects.model.StudentRetakeSubjectsId;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class TemporaryRetakeSubjectsDAOImpl implements TemporaryRetakeSubjectsDAO {

    @Override
    public boolean exists(String studentId, String subjectId) {
        String jpql = """
        SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END 
        FROM TemporaryRetakeSubjects t 
        WHERE t.student.id = :studentId 
          AND t.subject.subjectId = :subjectId
        """;
        return em.createQuery(jpql, Boolean.class)
                .setParameter("studentId", studentId)
                .setParameter("subjectId", subjectId)
                .getSingleResult();
    }

    @PersistenceContext
    private EntityManager em;

    @Override
    public void save(TemporaryRetakeSubjects entity) {
        if (entity.getId() == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        em.merge(entity); // merge để upsert an toàn
    }

    @Override
    public TemporaryRetakeSubjects findById(StudentRetakeSubjectsId id) {
        return em.find(TemporaryRetakeSubjects.class, id);
    }

    @Override
    public List<TemporaryRetakeSubjects> findAllPending() {
        return em.createQuery("""
            SELECT t FROM TemporaryRetakeSubjects t 
            LEFT JOIN FETCH t.student 
            LEFT JOIN FETCH t.subject 
            WHERE t.processed = false 
            ORDER BY t.createdAt DESC
            """, TemporaryRetakeSubjects.class)
                .getResultList();
    }

    @Override
    public List<TemporaryRetakeSubjects> findByStudentId(String studentId) {
        return em.createQuery("""
            SELECT t FROM TemporaryRetakeSubjects t 
            LEFT JOIN FETCH t.student 
            LEFT JOIN FETCH t.subject 
            WHERE t.student.id = :studentId
            """, TemporaryRetakeSubjects.class)
                .setParameter("studentId", studentId)
                .getResultList();
    }

    @Override
    public void markAsProcessed(String studentId, String subjectId) {
        em.createQuery("""
            UPDATE TemporaryRetakeSubjects t 
            SET t.processed = true 
            WHERE t.student.id = :studentId 
              AND t.subject.subjectId = :subjectId
            """)
                .setParameter("studentId", studentId)
                .setParameter("subjectId", subjectId)
                .executeUpdate();
    }

    @Override
    public void deleteProcessedRecords() {
        em.createQuery("DELETE FROM TemporaryRetakeSubjects t WHERE t.processed = true")
                .executeUpdate();
    }
    @Override
    public void deleteByStudentAndSubject(String studentId, String subjectId) {
        em.createQuery(
                        "DELETE FROM TemporaryRetakeSubjects t " +
                                "WHERE t.student.id = :studentId AND t.subject.subjectId = :subjectId")
                .setParameter("studentId", studentId)
                .setParameter("subjectId", subjectId)
                .executeUpdate();
    }
}