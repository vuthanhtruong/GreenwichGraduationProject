package com.example.demo.AcademicTranscript.dao;

import com.example.demo.AcademicTranscript.model.MajorAcademicTranscripts;
import com.example.demo.AcademicTranscript.model.MinorAcademicTranscripts;
import com.example.demo.student.model.Students;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional
public class AcademicTranscriptsDAOImpl implements AcademicTranscriptsDAO {
    private static final Logger log = LoggerFactory.getLogger(AcademicTranscriptsDAOImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<MajorAcademicTranscripts> getMajorAcademicTranscripts(Students student) {
        if (student == null) {
            log.debug("Received null student for MajorAcademicTranscripts query");
            return new ArrayList<>();
        }
        try {
            List<MajorAcademicTranscripts> transcripts = entityManager
                    .createQuery(
                            "SELECT m FROM MajorAcademicTranscripts m " +
                                    "JOIN Classes c ON m.subject.id = c.id " +
                                    "WHERE m.student = :student", MajorAcademicTranscripts.class)
                    .setParameter("student", student)
                    .getResultList();
            return transcripts != null ? transcripts : new ArrayList<>();
        } catch (Exception e) {
            log.error("Error fetching MajorAcademicTranscripts for student ID: {}",
                    student.getId(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<MinorAcademicTranscripts> getMinorAcademicTranscripts(Students student) {
        if (student == null) {
            log.debug("Received null student for MinorAcademicTranscripts query");
            return new ArrayList<>();
        }
        try {
            List<MinorAcademicTranscripts> transcripts = entityManager
                    .createQuery(
                            "SELECT m FROM MinorAcademicTranscripts m " +
                                    "JOIN Classes c ON m.subject.id = c.id " +
                                    "WHERE m.student = :student", MinorAcademicTranscripts.class)
                    .setParameter("student", student)
                    .getResultList();
            return transcripts != null ? transcripts : new ArrayList<>();
        } catch (Exception e) {
            log.error("Error fetching MinorAcademicTranscripts for student ID: {}",
                    student.getId(), e);
            return new ArrayList<>();
        }
    }
}