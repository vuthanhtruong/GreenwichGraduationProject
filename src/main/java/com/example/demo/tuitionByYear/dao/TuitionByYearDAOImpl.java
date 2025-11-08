package com.example.demo.tuitionByYear.dao;

import com.example.demo.curriculum.model.Curriculum;
import com.example.demo.major.model.Majors;
import com.example.demo.subject.majorSubject.model.MajorSubjects;
import com.example.demo.subject.majorSubject.service.MajorSubjectsService;
import com.example.demo.subject.minorSubject.model.MinorSubjects;
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
                                "AND t.tuition > 0 " ,
                        TuitionByYear.class)
                .setParameter("admissionYear", admissionYear)
                .setParameter("campus", campus)
                .getResultList();
    }

    @Override
    public List<MajorSubjects> getMajorSubjectsWithTuitionByYearAndCurriculum(Integer admissionYear, Curriculum curriculum, Majors major, Campuses campus) {

        List<TuitionByYear> tuitions = getTuitionsWithFeeByYearAndCampus(admissionYear, campus);
        List<MajorSubjects> majorSubjects = new ArrayList<>();

        for (TuitionByYear tuition : tuitions) {
            MajorSubjects subject =majorSubjectsService.getSubjectById(tuition.getSubject().getSubjectId());
            if(subject!=null && subject.getMajor().equals(major) && (subject.getCurriculum().getCurriculumId() == curriculum.getCurriculumId())) {
                majorSubjects.add(subject);
            }
        }
        return majorSubjects;
    }

    @Override
    public List<SpecializedSubject> getSpecializedSubjectsWithTuitionByYearAndCurriculum(Integer admissionYear, Curriculum curriculum, Majors major, Campuses campus) {

        List<TuitionByYear> tuitions = getTuitionsWithFeeByYearAndCampus(admissionYear, campus);
        List<SpecializedSubject> SpecializedSubjects = new ArrayList<>();

        for (TuitionByYear tuition : tuitions) {
            SpecializedSubject subject=specializedSubjectsService.getSubjectById(tuition.getSubject().getSubjectId());
            if(subject!=null && subject.getSpecialization().getMajor().equals(major) && (subject.getCurriculum().getCurriculumId() == curriculum.getCurriculumId())) {
                SpecializedSubjects.add(subject);
            }
        }
        return SpecializedSubjects;
    }
    @Override
    public List<Integer> findAllAdmissionYearsWithSpecializedTuition(String campusId, Majors major) {
        if (campusId == null || campusId.isBlank() || major == null) {
            return List.of();
        }

        return entityManager.createQuery("""
        SELECT DISTINCT t.id.admissionYear
        FROM TuitionByYear t
        WHERE t.campus.campusId = :campusId
          AND TREAT(t.subject AS SpecializedSubject).specialization.major = :major
          AND t.tuition IS NOT NULL
          AND t.tuition > 0
        ORDER BY t.id.admissionYear DESC
        """, Integer.class)
                .setParameter("campusId", campusId)
                .setParameter("major", major)
                .getResultList();
    }

    @Override
    public List<Integer> findAllAdmissionYearsWithMajorTuition(Campuses campus, Majors major) {
        if (campus == null) {
            throw new IllegalArgumentException("Campus cannot be null");
        }

        String jpql = """
        SELECT DISTINCT t.id.admissionYear
        FROM TuitionByYear t
        WHERE t.campus = :campus
          AND TREAT(t.subject AS MajorSubjects).major = :major
          AND t.tuition IS NOT NULL
          AND t.tuition > 0
        ORDER BY t.id.admissionYear DESC
        """;

        return entityManager.createQuery(jpql, Integer.class)
                .setParameter("campus", campus)
                .setParameter("major", major)
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
                                "AND (t.tuition IS NULL OR t.tuition <= 0) ",
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
                                "AND t.reStudyTuition > 0 ",
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
                                "AND (t.reStudyTuition IS NULL OR t.reStudyTuition <= 0) ",
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
    @Override
    public List<Integer> findAllAdmissionYearsWithMinorTuition(Campuses campus) {
        if (campus == null) {
            throw new IllegalArgumentException("Campus cannot be null");
        }

        List<TuitionByYear> tuitions = entityManager.createQuery(
                        "SELECT t FROM TuitionByYear t " +
                                "WHERE t.campus = :campus " +
                                "AND t.tuition IS NOT NULL " +
                                "AND t.tuition > 0",
                        TuitionByYear.class)
                .setParameter("campus", campus)
                .getResultList();

        List<Integer> years = new ArrayList<>();
        for (TuitionByYear t : tuitions) {
            MinorSubjects subject = minorSubjectsService.getSubjectById(t.getSubject().getSubjectId());
            if (subject != null && !years.contains(t.getId().getAdmissionYear())) {
                years.add(t.getId().getAdmissionYear());
            }
        }
        years.sort((a, b) -> Integer.compare(b, a)); // DESC
        return years;
    }

    @Override
    public List<MinorSubjects> getMinorSubjectsWithTuitionByYear(Integer admissionYear, Campuses campus) {
        if (admissionYear == null || campus == null) {
            throw new IllegalArgumentException("Admission year and campus cannot be null");
        }

        List<TuitionByYear> tuitions = getTuitionsWithFeeByYearAndCampus(admissionYear, campus);
        List<MinorSubjects> minorSubjects = new ArrayList<>();

        for (TuitionByYear tuition : tuitions) {
            MinorSubjects subject = minorSubjectsService.getSubjectById(tuition.getSubject().getSubjectId());
            if (subject != null) {
                minorSubjects.add(subject);
            }
        }
        return minorSubjects;
    }
}