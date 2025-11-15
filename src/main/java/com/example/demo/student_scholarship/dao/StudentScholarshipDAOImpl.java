package com.example.demo.student_scholarship.dao;
import com.example.demo.entity.Enums.ActivityStatus;
import com.example.demo.scholarship.model.Scholarships;
import com.example.demo.scholarshipByYear.model.ScholarshipByYear;
import com.example.demo.student_scholarship.model.Students_Scholarships;
import com.example.demo.scholarship.service.ScholarshipsService;
import com.example.demo.user.student.model.Students;
import com.example.demo.user.student.service.StudentsService;
import com.example.demo.user.staff.service.StaffsService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Transactional
public class StudentScholarshipDAOImpl implements StudentScholarshipDAO {

    @Override
    public Map<String, Object> getScholarshipByStudentId(String studentId) {
        if (studentId == null || studentId.trim().isEmpty()) {
            return null;
        }

        Object[] row = entityManager.createQuery(
                        "SELECT ss, sby " +
                                "FROM Students_Scholarships ss " +
                                "JOIN ScholarshipByYear sby " +
                                "  ON sby.id.scholarshipId = ss.scholarship.scholarshipId " +
                                " AND sby.id.admissionYear = ss.admissionYear " +
                                "WHERE ss.student.id = :studentId " +
                                "  AND ss.status = 'ACTIVATED'",
                        Object[].class)
                .setParameter("studentId", studentId)
                .getResultList()
                .stream()
                .findFirst()
                .orElse(null);

        if (row == null) {
            return null;
        }

        Students_Scholarships ss = (Students_Scholarships) row[0];
        ScholarshipByYear sby = (ScholarshipByYear) row[1];

        Map<String, Object> details = new HashMap<>();
        details.put("scholarshipId", ss.getScholarship().getScholarshipId());
        details.put("scholarshipName", ss.getScholarship().getTypeName());
        details.put("admissionYear", ss.getAdmissionYear());
        details.put("awardDate", ss.getAwardDate());
        details.put("discountPercentage", sby.getDiscountPercentage());
        details.put("status", ss.getStatus().name());
        details.put("createdAt", ss.getCreatedAt());

        return details;
    }

    @Override
    public Long getCountStudentScholarshipByYear(Integer admissionYear, Scholarships scholarship) {
        if (admissionYear == null || scholarship == null) {
            return 0L;
        }
        return entityManager.createQuery(
                        "SELECT COUNT(ss) FROM Students_Scholarships ss " +
                                "WHERE ss.admissionYear = :admissionYear AND ss.scholarship.scholarshipId = :scholarshipId",
                        Long.class)
                .setParameter("admissionYear", admissionYear)
                .setParameter("scholarshipId", scholarship.getScholarshipId())
                .getSingleResult();
    }

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
    public Map<String, Map<String, Object>> getAwardedScholarshipsByYear(Integer admissionYear) {
        Map<String, Map<String, Object>> awardMap = new HashMap<>();

        if (admissionYear == null) {
            return awardMap;
        }

        List<Object[]> results = entityManager.createQuery(
                        "SELECT ss, sby " +
                                "FROM Students_Scholarships ss " +
                                "JOIN ScholarshipByYear sby " +
                                "  ON sby.scholarship.scholarshipId = ss.scholarship.scholarshipId " +
                                " AND sby.id.admissionYear = ss.admissionYear " +
                                "WHERE ss.admissionYear = :admissionYear",
                        Object[].class)
                .setParameter("admissionYear", admissionYear)
                .getResultList();

        for (Object[] row : results) {
            Students_Scholarships ss = (Students_Scholarships) row[0];
            ScholarshipByYear sby = (ScholarshipByYear) row[1];

            // tạo key duy nhất: studentId + scholarshipId
            String key = ss.getStudent().getId() + "-" + ss.getScholarship().getScholarshipId();

            Map<String, Object> details = new HashMap<>();
            details.put("studentId", ss.getStudent().getId());
            details.put("studentName", ss.getStudent().getFullName());
            details.put("scholarshipName", ss.getScholarship().getTypeName());
            details.put("awardDate", ss.getAwardDate());
            details.put("scholarship", ss.getScholarship().getScholarshipId());
            details.put("discountPercentage", sby.getDiscountPercentage());

            awardMap.put(key, details);
        }

        return awardMap;
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