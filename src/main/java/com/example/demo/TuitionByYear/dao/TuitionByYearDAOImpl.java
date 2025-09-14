package com.example.demo.TuitionByYear.dao;

import com.example.demo.TuitionByYear.model.TuitionByYear;
import com.example.demo.TuitionByYear.model.TuitionByYearId;
import com.example.demo.admin.model.Admins;
import com.example.demo.admin.service.AdminsService;
import com.example.demo.campus.model.Campuses;
import com.example.demo.subject.model.Subjects;
import com.example.demo.subject.service.SubjectsService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class TuitionByYearDAOImpl implements TuitionByYearDAO {
    private final AdminsService adminsService;
    private final SubjectsService subjectsService;

    @PersistenceContext
    private EntityManager entityManager;

    public TuitionByYearDAOImpl(AdminsService adminsService, SubjectsService subjectsService) {
        this.adminsService = adminsService;
        this.subjectsService = subjectsService;
    }

    @Override
    public TuitionByYear findById(TuitionByYearId id) {
        return entityManager.find(TuitionByYear.class, id);
    }

    @Override
    public void updateTuition(TuitionByYear tuition) {

        entityManager.merge(tuition);
    }

    @Override
    public void createTuition(TuitionByYear tuition) {

        entityManager.persist(tuition);
    }



    @Override
    public List<TuitionByYear> getTuitionsByYear(Integer admissionYear) {
        if (admissionYear == null) {
            throw new IllegalArgumentException("Admission year cannot be null");
        }
        Campuses adminCampus = adminsService.getAdminCampus();
        if (adminCampus == null) {
            throw new IllegalStateException("Admin's campus not found.");
        }
        return entityManager.createQuery(
                        "SELECT t FROM TuitionByYear t JOIN FETCH t.subject JOIN FETCH t.campus JOIN FETCH t.creator " +
                                "WHERE t.id.admissionYear = :admissionYear AND t.id.campusId = :campusId",
                        TuitionByYear.class)
                .setParameter("admissionYear", admissionYear)
                .setParameter("campusId", adminCampus.getCampusId())
                .getResultList();
    }

    @Override
    public List<Integer> getAllAdmissionYears() {
        Campuses adminCampus = adminsService.getAdminCampus();
        if (adminCampus == null) {
            throw new IllegalStateException("Admin's campus not found.");
        }
        return entityManager.createQuery(
                        "SELECT DISTINCT t.id.admissionYear FROM TuitionByYear t WHERE t.id.campusId = :campusId ORDER BY t.id.admissionYear DESC",
                        Integer.class)
                .setParameter("campusId", adminCampus.getCampusId())
                .getResultList();
    }

    public List<TuitionByYear> findByAdmissionYear(Integer admissionYear) {
        if (admissionYear == null) {
            throw new IllegalArgumentException("Admission year cannot be null");
        }
        Campuses adminCampus = adminsService.getAdminCampus();
        if (adminCampus == null) {
            throw new IllegalStateException("Admin's campus not found.");
        }
        return entityManager.createQuery(
                        "SELECT t FROM TuitionByYear t JOIN FETCH t.subject JOIN FETCH t.campus JOIN FETCH t.creator " +
                                "WHERE t.id.admissionYear = :admissionYear AND t.id.campusId = :campusId",
                        TuitionByYear.class)
                .setParameter("admissionYear", admissionYear)
                .setParameter("campusId", adminCampus.getCampusId())
                .getResultList();
    }

    public List<Integer> findAllAdmissionYears() {
        Campuses adminCampus = adminsService.getAdminCampus();
        if (adminCampus == null) {
            throw new IllegalStateException("Admin's campus not found.");
        }
        return entityManager.createQuery(
                        "SELECT DISTINCT t.id.admissionYear FROM TuitionByYear t WHERE t.id.campusId = :campusId ORDER BY t.id.admissionYear DESC",
                        Integer.class)
                .setParameter("campusId", adminCampus.getCampusId())
                .getResultList();
    }
}