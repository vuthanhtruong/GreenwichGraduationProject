package com.example.demo.tuitionByYear.dao;

import com.example.demo.tuitionByYear.model.TuitionByYear;
import com.example.demo.tuitionByYear.model.TuitionByYearId;
import com.example.demo.user.admin.service.AdminsService;
import com.example.demo.campus.model.Campuses;
import com.example.demo.entity.Enums.ContractStatus;
import com.example.demo.subject.abstractSubject.service.SubjectsService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class TuitionByYearDAOImpl implements TuitionByYearDAO {

    @PersistenceContext
    private EntityManager entityManager;

    private final AdminsService adminsService;
    private final SubjectsService subjectsService;

    public TuitionByYearDAOImpl(AdminsService adminsService, SubjectsService subjectsService) {
        this.adminsService = adminsService;
        this.subjectsService = subjectsService;
    }

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
        if (tuition.getCreator() == null) {
            tuition.setCreator(adminsService.getAdmin());
        }
        entityManager.merge(tuition);
    }

    @Override
    public void createTuition(TuitionByYear tuition) {
        if (tuition.getCreator() == null) {
            tuition.setCreator(adminsService.getAdmin());
        }
        if (tuition.getId() == null) {
            throw new IllegalArgumentException("TuitionByYear ID cannot be null");
        }
        entityManager.persist(tuition);
    }

    @Override
    public List<TuitionByYear> getTuitionsWithFeeByYear(Integer admissionYear) {
        if (admissionYear == null) {
            throw new IllegalArgumentException("Admission year cannot be null");
        }
        Campuses adminCampus = adminsService.getAdminCampus();
        if (adminCampus == null) {
            throw new IllegalStateException("Admin's campus not found.");
        }

        return entityManager.createQuery(
                        "SELECT t FROM TuitionByYear t " +
                                "WHERE t.id.admissionYear = :admissionYear " +
                                "AND t.id.campusId = :campusId " +
                                "AND t.tuition IS NOT NULL " +
                                "AND t.tuition > 0 " +
                                "ORDER BY t.subject.requirementType",
                        TuitionByYear.class)
                .setParameter("admissionYear", admissionYear)
                .setParameter("campusId", adminCampus.getCampusId())
                .getResultList();
    }

    @Override
    public List<TuitionByYear> getTuitionsWithoutFeeByYear(Integer admissionYear) {
        if (admissionYear == null) {
            throw new IllegalArgumentException("Admission year cannot be null");
        }
        Campuses adminCampus = adminsService.getAdminCampus();
        if (adminCampus == null) {
            throw new IllegalStateException("Admin's campus not found.");
        }

        return entityManager.createQuery(
                        "SELECT t FROM TuitionByYear t " +
                                "WHERE t.id.admissionYear = :admissionYear " +
                                "AND t.id.campusId = :campusId " +
                                "AND (t.tuition IS NULL OR t.tuition <= 0) " +
                                "ORDER BY t.subject.requirementType",
                        TuitionByYear.class)
                .setParameter("admissionYear", admissionYear)
                .setParameter("campusId", adminCampus.getCampusId())
                .getResultList();
    }

    @Override
    public List<TuitionByYear> getTuitionsWithReStudyFeeByYear(Integer admissionYear) {
        if (admissionYear == null) {
            throw new IllegalArgumentException("Admission year cannot be null");
        }
        Campuses adminCampus = adminsService.getAdminCampus();
        if (adminCampus == null) {
            throw new IllegalStateException("Admin's campus not found.");
        }

        return entityManager.createQuery(
                        "SELECT t FROM TuitionByYear t " +
                                "WHERE t.id.admissionYear = :admissionYear " +
                                "AND t.id.campusId = :campusId " +
                                "AND t.reStudyTuition IS NOT NULL " +
                                "AND t.reStudyTuition > 0 " +
                                "ORDER BY t.subject.requirementType",
                        TuitionByYear.class)
                .setParameter("admissionYear", admissionYear)
                .setParameter("campusId", adminCampus.getCampusId())
                .getResultList();
    }

    @Override
    public List<TuitionByYear> getTuitionsWithoutReStudyFeeByYear(Integer admissionYear) {
        if (admissionYear == null) {
            throw new IllegalArgumentException("Admission year cannot be null");
        }
        Campuses adminCampus = adminsService.getAdminCampus();
        if (adminCampus == null) {
            throw new IllegalStateException("Admin's campus not found.");
        }

        return entityManager.createQuery(
                        "SELECT t FROM TuitionByYear t " +
                                "WHERE t.id.admissionYear = :admissionYear " +
                                "AND t.id.campusId = :campusId " +
                                "AND (t.reStudyTuition IS NULL OR t.reStudyTuition <= 0) " +
                                "ORDER BY t.subject.requirementType",
                        TuitionByYear.class)
                .setParameter("admissionYear", admissionYear)
                .setParameter("campusId", adminCampus.getCampusId())
                .getResultList();
    }

    @Override
    public List<Integer> findAllAdmissionYears() {
        Campuses adminCampus = adminsService.getAdminCampus();
        if (adminCampus == null) {
            throw new IllegalStateException("Admin's campus not found.");
        }
        return entityManager.createQuery(
                        "SELECT DISTINCT t.id.admissionYear FROM TuitionByYear t " +
                                "WHERE t.id.campusId = :campusId " +
                                "ORDER BY t.id.admissionYear DESC",
                        Integer.class)
                .setParameter("campusId", adminCampus.getCampusId())
                .getResultList();
    }

    @Override
    public List<TuitionByYear> findByAdmissionYear(Integer admissionYear) {
        if (admissionYear == null) {
            throw new IllegalArgumentException("Admission year cannot be null");
        }
        Campuses adminCampus = adminsService.getAdminCampus();
        if (adminCampus == null) {
            throw new IllegalStateException("Admin's campus not found.");
        }
        return entityManager.createQuery(
                        "SELECT t FROM TuitionByYear t " +
                                "JOIN FETCH t.subject " +
                                "JOIN FETCH t.campus " +
                                "JOIN FETCH t.creator " +
                                "WHERE t.id.admissionYear = :admissionYear " +
                                "AND t.id.campusId = :campusId",
                        TuitionByYear.class)
                .setParameter("admissionYear", admissionYear)
                .setParameter("campusId", adminCampus.getCampusId())
                .getResultList();
    }

    @Override
    public void finalizeContracts(Integer admissionYear) {
        if (admissionYear == null) {
            throw new IllegalArgumentException("Admission year cannot be null");
        }
        Campuses adminCampus = adminsService.getAdminCampus();
        if (adminCampus == null) {
            throw new IllegalStateException("Admin's campus not found.");
        }
        entityManager.createQuery(
                        "UPDATE TuitionByYear t SET t.contractStatus = :status " +
                                "WHERE t.id.admissionYear = :admissionYear " +
                                "AND t.id.campusId = :campusId " +
                                "AND t.tuition IS NOT NULL AND t.tuition > 0")
                .setParameter("status", ContractStatus.ACTIVE)
                .setParameter("admissionYear", admissionYear)
                .setParameter("campusId", adminCampus.getCampusId())
                .executeUpdate();
    }
}