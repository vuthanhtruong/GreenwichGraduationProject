package com.example.demo.scholarship.dao;
import com.example.demo.admin.service.AdminsService;
import com.example.demo.entity.Enums.ActivityStatus;
import com.example.demo.scholarship.model.ScholarshipByYear;
import com.example.demo.scholarship.model.Students_Scholarships;
import com.example.demo.scholarship.service.ScholarshipsService;
import com.example.demo.student.model.Students;
import com.example.demo.student.service.StudentsService;
import com.example.demo.scholarship.service.ScholarshipByYearService;
import com.example.demo.staff.service.StaffsService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional
public class StudentScholarshipDAOImpl implements StudentScholarshipDAO {

    private final StudentsService studentsService;
    private final StaffsService staffsService;
    private final ScholarshipsService scholarshipsService;

    public StudentScholarshipDAOImpl(StudentsService studentsService, StaffsService staffsService, ScholarshipsService scholarshipsService) {
        this.studentsService = studentsService;
        this.staffsService = staffsService;
        this.scholarshipsService = scholarshipsService;
    }

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public ScholarshipByYear getScholarshipByYear(String scholarshipId, Integer admissionYear) {
        if (scholarshipId == null || admissionYear == null) {
            return null;
        }
        return entityManager.createQuery(
                        "SELECT sby FROM ScholarshipByYear sby " +
                                "WHERE sby.id.scholarshipId = :scholarshipId AND sby.id.admissionYear = :admissionYear",
                        ScholarshipByYear.class)
                .setParameter("scholarshipId", scholarshipId)
                .setParameter("admissionYear", admissionYear)
                .getResultList()
                .stream()
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Students_Scholarships> getAwardedScholarshipsByYear(Integer admissionYear) {
        if (admissionYear == null) {
            return List.of();
        }
        return entityManager.createQuery(
                        "SELECT ss FROM Students_Scholarships ss "+
                                "WHERE ss.admissionYear = :admissionYear",
                        Students_Scholarships.class)
                .setParameter("admissionYear", admissionYear)
                .getResultList();
    }

    @Override
    public void assignScholarship(String studentId, String scholarshipId, Integer admissionYear) {
        Students student = studentsService.getStudentById(studentId);
        if (student == null) {
            throw new IllegalArgumentException("Student with ID " + studentId + " not found");
        }


        ScholarshipByYear scholarshipByYear = getScholarshipByYear(scholarshipId, admissionYear);
        if (scholarshipByYear == null) {
            throw new IllegalArgumentException("Scholarship with ID " + scholarshipId + " for year " + admissionYear + " not found");
        }

        Students_Scholarships existingAward = entityManager.createQuery(
                        "SELECT ss FROM Students_Scholarships ss " +
                                "WHERE ss.student.id = :studentId ",
                        Students_Scholarships.class)
                .setParameter("studentId", studentId)
                .getResultList()
                .stream()
                .findFirst()
                .orElse(null);

        if (existingAward != null) {
            throw new IllegalStateException("Student " + studentId + " already awarded scholarship " + scholarshipId + " for year " + admissionYear);
        }

        Students_Scholarships studentScholarship = new Students_Scholarships();
        studentScholarship.setStudent(student);
        studentScholarship.setStudentId(studentId);
        studentScholarship.setScholarship(scholarshipsService.getScholarshipById(scholarshipId));
        studentScholarship.setAwardDate(LocalDate.now());
        studentScholarship.setStatus(ActivityStatus.ACTIVATED);
        studentScholarship.setCreator(staffsService.getStaff());
        studentScholarship.setCreatedAt(LocalDateTime.now());
        studentScholarship.setAdmissionYear(admissionYear);

        entityManager.persist(studentScholarship);

    }

    @Override
    public List<String> validateScholarshipAward(String studentId, String scholarshipId, Integer admissionYear) {
        List<String> errors = new ArrayList<>();

        if (studentId == null || studentId.trim().isEmpty()) {
            errors.add("Student ID is required.");
        } else if (studentsService.getStudentById(studentId) == null) {
            errors.add("Student ID " + studentId + " does not exist.");
        }

        if (scholarshipId == null || scholarshipId.trim().isEmpty()) {
            errors.add("Scholarship ID is required.");
        } else if (getScholarshipByYear(scholarshipId, admissionYear) == null) {
            errors.add("Scholarship ID " + scholarshipId + " for year " + admissionYear + " does not exist.");
        }

        if (errors.isEmpty()) {
            Students_Scholarships existingAward = entityManager.createQuery(
                            "SELECT ss FROM Students_Scholarships ss " +
                                    "WHERE ss.student.id = :studentId ",
                            Students_Scholarships.class)
                    .setParameter("studentId", studentId)
                    .getResultList()
                    .stream()
                    .findFirst()
                    .orElse(null);
            if (existingAward != null) {
                errors.add("Student " + studentId + " already awarded scholarship " + scholarshipId + " for year " + admissionYear);
            }
        }

        return errors;
    }
}