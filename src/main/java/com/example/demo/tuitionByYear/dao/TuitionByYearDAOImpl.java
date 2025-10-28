package com.example.demo.tuitionByYear.dao;

import com.example.demo.curriculum.model.Curriculum;
import com.example.demo.subject.majorSubject.model.MajorSubjects;
import com.example.demo.subject.majorSubject.service.MajorSubjectsService;
import com.example.demo.subject.minorSubject.service.MinorSubjectsService;
import com.example.demo.subject.specializedSubject.model.SpecializedSubject;
import com.example.demo.subject.specializedSubject.service.SpecializedSubjectsService;
import com.example.demo.tuitionByYear.model.TuitionByYear;
import com.example.demo.tuitionByYear.model.TuitionByYearId;
import com.example.demo.campus.model.Campuses;
import com.example.demo.entity.Enums.ContractStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional
public class TuitionByYearDAOImpl implements TuitionByYearDAO {

    @PersistenceContext
    private EntityManager entityManager;

    private final MajorSubjectsService majorSubjectsService;
    private final SpecializedSubjectsService specializedSubjectsService;
    private final MinorSubjectsService minorSubjectsService;

    public TuitionByYearDAOImpl(MajorSubjectsService majorSubjectsService, SpecializedSubjectsService specializedSubjectsService, MinorSubjectsService minorSubjectsService) {
        this.majorSubjectsService = majorSubjectsService;
        this.specializedSubjectsService = specializedSubjectsService;
        this.minorSubjectsService = minorSubjectsService;
    }

    // REMOVED AdminsService – no more hidden campus dependency

    @Override
    public List<TuitionByYear> tuitionFeesByCampus(String campusId, Integer admissionYear) {
        return entityManager.createQuery(
                        "SELECT t FROM TuitionByYear t " +
                                "WHERE t.campus.campusId = :campusId " +
                                "AND t.admissionYear = :admissionYear " +
                                "ORDER BY t.subject.subjectId", TuitionByYear.class)
                .setParameter("campusId", campusId)
                .setParameter("admissionYear", admissionYear)
                .getResultList();
    }

    @Override
    public TuitionByYear findById(TuitionByYearId id) {
        return entityManager.find(TuitionByYear.class, id);
    }

    @Override
    public void updateTuition(TuitionByYear tuition) {
        // Creator must be set by caller
        entityManager.merge(tuition);
    }

    @Override
    public void createTuition(TuitionByYear tuition) {
        if (tuition.getId() == null) {
            throw new IllegalArgumentException("TuitionByYear ID cannot be null");
        }
        // Creator must be set by caller
        entityManager.persist(tuition);
    }

    @Override
    public List<TuitionByYear> getTuitionsWithFeeByYearAndCampus(Integer admissionYear, Campuses campus) {
        if (admissionYear == null || campus == null) {
            throw new IllegalArgumentException("Admission year and campus cannot be null");
        }

        return entityManager.createQuery(
                        "SELECT t FROM TuitionByYear t " +
                                "WHERE t.id.admissionYear = :admissionYear " +
                                "AND t.campus = :campus " +
                                "AND t.tuition IS NOT NULL " +
                                "AND t.tuition > 0 " +
                                "ORDER BY t.subject.requirementType",
                        TuitionByYear.class)
                .setParameter("admissionYear", admissionYear)
                .setParameter("campus", campus)
                .getResultList();
    }

    @Override
    public List<MajorSubjects> getMajorSubjectsWithTuitionByYearAndCurriculum(Integer admissionYear, Curriculum curriculum, Campuses campus) {

        List<TuitionByYear> tuitions = getTuitionsWithFeeByYearAndCampus(admissionYear, campus);
        List<MajorSubjects> majorSubjects = new ArrayList<>();

        for (TuitionByYear tuition : tuitions) {
            MajorSubjects subjet =majorSubjectsService.getSubjectById(tuition.getSubject().getSubjectId());
            if(subjet != null && (subjet.getCurriculum().getCurriculumId() == curriculum.getCurriculumId())) {
                majorSubjects.add(subjet);
            }
        }
        return majorSubjects;
    }
    @Override
    public List<SpecializedSubject> getSpecializedSubjectsWithTuitionByYearAndCurriculum(Integer admissionYear, Curriculum curriculum, Campuses campus) {

        List<TuitionByYear> tuitions = getTuitionsWithFeeByYearAndCampus(admissionYear, campus);
        List<SpecializedSubject> SpecializedSubjects = new ArrayList<>();

        for (TuitionByYear tuition : tuitions) {
            SpecializedSubject subject=specializedSubjectsService.getSubjectById(tuition.getSubject().getSubjectId());
            if(subject != null && (subject.getCurriculum().getCurriculumId() == curriculum.getCurriculumId())) {
                SpecializedSubjects.add(subject);
            }
        }
        return SpecializedSubjects;
    }
    @Override
    public List<Integer> findAllAdmissionYearsWithSpecializedTuition(String campusId) {
        if (campusId == null || campusId.isBlank()) return List.of();

        return entityManager.createQuery("""
        SELECT DISTINCT t.id.admissionYear
        FROM TuitionByYear t
        WHERE t.campus.campusId = :campusId
          AND TYPE(t.subject) = SpecializedSubject
          AND t.tuition IS NOT NULL
          AND t.tuition > 0
        ORDER BY t.id.admissionYear DESC
        """, Integer.class)
                .setParameter("campusId", campusId)
                .getResultList();
    }

    @Override
    public List<Integer> findAllAdmissionYearsWithMajorTuition(Campuses campus) {
        if (campus == null) {
            throw new IllegalArgumentException("Campus cannot be null");
        }

        return entityManager.createQuery(
                        "SELECT DISTINCT t.id.admissionYear " +
                                "FROM TuitionByYear t " +
                                "WHERE t.campus = :campus " +
                                "AND TYPE(t.subject) = MajorSubjects " +
                                "AND t.tuition IS NOT NULL " +
                                "AND t.tuition > 0 " +
                                "ORDER BY t.id.admissionYear DESC",
                        Integer.class)
                .setParameter("campus", campus)
                .getResultList();
    }

    @Override
    public List<TuitionByYear> getTuitionsWithoutFeeByYear(Integer admissionYear, Campuses campus) {
        if (admissionYear == null || campus == null) {
            throw new IllegalArgumentException("Admission year and campus cannot be null");
        }

        return entityManager.createQuery(
                        "SELECT t FROM TuitionByYear t " +
                                "WHERE t.id.admissionYear = :admissionYear " +
                                "AND t.campus = :campus " +
                                "AND (t.tuition IS NULL OR t.tuition <= 0) " +
                                "ORDER BY t.subject.requirementType",
                        TuitionByYear.class)
                .setParameter("admissionYear", admissionYear)
                .setParameter("campus", campus)
                .getResultList();
    }

    @Override
    public List<TuitionByYear> getTuitionsWithReStudyFeeByYear(Integer admissionYear, Campuses campus) {
        if (admissionYear == null || campus == null) {
            throw new IllegalArgumentException("Admission year and campus cannot be null");
        }

        return entityManager.createQuery(
                        "SELECT t FROM TuitionByYear t " +
                                "WHERE t.id.admissionYear = :admissionYear " +
                                "AND t.campus = :campus " +
                                "AND t.reStudyTuition IS NOT NULL " +
                                "AND t.reStudyTuition > 0 " +
                                "ORDER BY t.subject.requirementType",
                        TuitionByYear.class)
                .setParameter("admissionYear", admissionYear)
                .setParameter("campus", campus)
                .getResultList();
    }

    @Override
    public List<TuitionByYear> getTuitionsWithoutReStudyFeeByYear(Integer admissionYear, Campuses campus) {
        if (admissionYear == null || campus == null) {
            throw new IllegalArgumentException("Admission year and campus cannot be null");
        }

        return entityManager.createQuery(
                        "SELECT t FROM TuitionByYear t " +
                                "WHERE t.id.admissionYear = :admissionYear " +
                                "AND t.campus = :campus " +
                                "AND (t.reStudyTuition IS NULL OR t.reStudyTuition <= 0) " +
                                "ORDER BY t.subject.requirementType",
                        TuitionByYear.class)
                .setParameter("admissionYear", admissionYear)
                .setParameter("campus", campus)
                .getResultList();
    }

    @Override
    public List<Integer> findAllAdmissionYears(Campuses campus) {
        if (campus == null) {
            throw new IllegalArgumentException("Campus cannot be null");
        }

        return entityManager.createQuery(
                        "SELECT DISTINCT t.id.admissionYear FROM TuitionByYear t " +
                                "WHERE t.campus = :campus " +
                                "ORDER BY t.id.admissionYear DESC",
                        Integer.class)
                .setParameter("campus", campus)
                .getResultList();
    }

    @Override
    public List<TuitionByYear> findByAdmissionYear(Integer admissionYear, Campuses campus) {
        if (admissionYear == null || campus == null) {
            throw new IllegalArgumentException("Admission year and campus cannot be null");
        }

        return entityManager.createQuery(
                        "SELECT t FROM TuitionByYear t " +
                                "JOIN FETCH t.subject " +
                                "JOIN FETCH t.campus " +
                                "JOIN FETCH t.creator " +
                                "WHERE t.id.admissionYear = :admissionYear " +
                                "AND t.campus = :campus",
                        TuitionByYear.class)
                .setParameter("admissionYear", admissionYear)
                .setParameter("campus", campus)
                .getResultList();
    }

    @Override
    public void finalizeContracts(Integer admissionYear, Campuses campus) {
        if (admissionYear == null || campus == null) {
            throw new IllegalArgumentException("Admission year and campus cannot be null");
        }

        entityManager.createQuery(
                        "UPDATE TuitionByYear t SET t.contractStatus = :status " +
                                "WHERE t.id.admissionYear = :admissionYear " +
                                "AND t.campus = :campus " +
                                "AND t.tuition > 0 " +
                                "AND t.reStudyTuition > 0 " +
                                "AND (t.contractStatus IS NULL OR t.contractStatus != :status)")
                .setParameter("status", ContractStatus.ACTIVE)
                .setParameter("admissionYear", admissionYear)
                .setParameter("campus", campus)
                .executeUpdate();
    }
}