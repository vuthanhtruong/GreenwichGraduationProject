package com.example.demo.TuitionByYear.dao;

import com.example.demo.TuitionByYear.model.TuitionByYear;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class TuitionByYearDAOImpl implements TuitionByYearDAO {
    @Override
    public List<TuitionByYear> getTuitionsByYear(Integer admissionYear) {
        if (admissionYear == null) {
            throw new IllegalArgumentException("Admission year cannot be null");
        }
        return entityManager.createQuery(
                        "SELECT t FROM TuitionByYear t WHERE t.id.admissionYear = :admissionYear",
                        TuitionByYear.class)
                .setParameter("admissionYear", admissionYear)
                .getResultList();
    }

    @Override
    public List<Integer> getAllAdmissionYears() {
        return entityManager.createQuery(
                        "SELECT DISTINCT t.id.admissionYear FROM TuitionByYear t ORDER BY t.id.admissionYear DESC",
                        Integer.class)
                .getResultList();
    }

    @PersistenceContext
    private EntityManager entityManager;

    public List<TuitionByYear> findByAdmissionYear(Integer admissionYear) {
        return entityManager.createQuery(
                        "SELECT t FROM TuitionByYear t WHERE t.id.admissionYear = :admissionYear",
                        TuitionByYear.class)
                .setParameter("admissionYear", admissionYear)
                .getResultList();
    }

    public List<Integer> findAllAdmissionYears() {
        return entityManager.createQuery(
                        "SELECT DISTINCT t.id.admissionYear FROM TuitionByYear t ORDER BY t.id.admissionYear DESC",
                        Integer.class)
                .getResultList();
    }
}
