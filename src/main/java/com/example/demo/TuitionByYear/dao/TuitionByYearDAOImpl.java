package com.example.demo.TuitionByYear.dao;

import com.example.demo.TuitionByYear.model.TuitionByYear;
import com.example.demo.admin.service.AdminsService;
import com.example.demo.entity.TuitionByYearId;
import com.example.demo.subject.model.Subjects;
import com.example.demo.subject.service.SubjectsService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@Transactional
public class TuitionByYearDAOImpl implements TuitionByYearDAO {
    private final AdminsService  adminsService;
    private final SubjectsService  subjectsService;

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
        if(tuition.getCreator()==null){
            Subjects subjects=subjectsService.getSubjectById(tuition.getSubject().getSubjectId());
            subjects.setAcceptor(adminsService.getAdmin());
        }
        tuition.setCreator(adminsService.getAdmin());
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
                .getResultList()  .stream()
                .distinct()
                .sorted()
                .collect(Collectors.toList());
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
