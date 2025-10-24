package com.example.demo.academicTranscript.dao;

import com.example.demo.academicTranscript.model.MajorAcademicTranscripts;
import com.example.demo.academicTranscript.model.MinorAcademicTranscripts;
import com.example.demo.academicTranscript.model.SpecializedAcademicTranscripts;
import com.example.demo.classes.majorClasses.model.MajorClasses;
import com.example.demo.classes.majorClasses.model.MinorClasses;
import com.example.demo.classes.specializedClasses.model.SpecializedClasses;
import com.example.demo.entity.Enums.Grades;
import com.example.demo.user.student.model.Students;
import com.example.demo.students_Classes.abstractStudents_Class.model.Students_Classes;
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
    @Override
    public List<MajorAcademicTranscripts> getAcademicTranscriptsByMajorClass(Students student, MajorClasses majorClass) {
        return entityManager.createQuery("from MajorAcademicTranscripts m where m.student=:student and m.majorClass=:majorClass").
                setParameter("majorClass", majorClass).
                setParameter("student", student).getResultList();

    }

    @Override
    public List<MinorAcademicTranscripts> getAcademicTranscriptsByMinorClass(Students student, MinorClasses minorClass) {
        return entityManager.createQuery("from MinorAcademicTranscripts m where m.student = :student and m.minorClass = :minorClass", MinorAcademicTranscripts.class)
                .setParameter("student", student)
                .setParameter("minorClass", minorClass)
                .getResultList();
    }

    @Override
    public List<SpecializedAcademicTranscripts> getAcademicTranscriptsBySpecializedClass(Students student, SpecializedClasses specializedClass) {
        return entityManager.createQuery("from SpecializedAcademicTranscripts s where s.student = :student and s.specializedClass = :specializedClass", SpecializedAcademicTranscripts.class)
                .setParameter("student", student)
                .setParameter("specializedClass", specializedClass)
                .getResultList();
    }

    @Override
    public List<Students_Classes> getLearningProcess(Students student) {
        return entityManager.createQuery("from Students_Classes sc where sc.student=:student", Students_Classes.class)
                .setParameter("student", student).getResultList();
    }

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
                                    "WHERE m.student = :student and m.grade!=:refer", MajorAcademicTranscripts.class)
                    .setParameter("student", student)
                    .setParameter("refer", Grades.REFER)
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
                                    "WHERE m.student = :student and m.grade!=:refer", MinorAcademicTranscripts.class)
                    .setParameter("student", student)
                    .setParameter("refer", Grades.REFER)
                    .getResultList();
            return transcripts != null ? transcripts : new ArrayList<>();
        } catch (Exception e) {
            log.error("Error fetching MinorAcademicTranscripts for student ID: {}",
                    student.getId(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<SpecializedAcademicTranscripts> getSpecializedAcademicTranscripts(Students student) {
        if (student == null) {
            log.debug("Received null student for SpecializedAcademicTranscripts query");
            return new ArrayList<>();
        }
        try {
            List<SpecializedAcademicTranscripts> transcripts = entityManager
                    .createQuery(
                            "SELECT s FROM SpecializedAcademicTranscripts s " +
                                    "WHERE s.student = :student AND s.grade != :refer", SpecializedAcademicTranscripts.class)
                    .setParameter("student", student)
                    .setParameter("refer", Grades.REFER)
                    .getResultList();
            return transcripts != null ? transcripts : new ArrayList<>();
        } catch (Exception e) {
            log.error("Error fetching SpecializedAcademicTranscripts for student ID: {}", student.getId(), e);
            return new ArrayList<>();
        }
    }
}