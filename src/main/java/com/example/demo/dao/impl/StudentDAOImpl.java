package com.example.demo.dao.impl;

import com.example.demo.dao.StudentsDAO;
import com.example.demo.entity.*;
import com.example.demo.service.AccountBalancesService;
import com.example.demo.service.EmailServiceForLectureService;
import com.example.demo.service.EmailServiceForStudentService;
import com.example.demo.service.StaffsService;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@Transactional
public class StudentDAOImpl implements StudentsDAO {

    private final StaffsService staffsService;
    @PersistenceContext
    private EntityManager entityManager;

    private final EmailServiceForStudentService emailServiceForStudentService;
    private final EmailServiceForLectureService emailServiceForLectureService;
    private final AccountBalancesService  accountBalancesService;

    public StudentDAOImpl(EmailServiceForStudentService emailServiceForStudentService,
                          EmailServiceForLectureService emailServiceForLectureService, AccountBalancesService accountBalancesService, StaffsService staffsService) {
        this.accountBalancesService = accountBalancesService;
        if (emailServiceForStudentService == null || emailServiceForLectureService == null) {
            throw new IllegalArgumentException("Email services cannot be null");
        }
        this.emailServiceForStudentService = emailServiceForStudentService;
        this.emailServiceForLectureService = emailServiceForLectureService;
        this.staffsService = staffsService;
    }

    @Override
    public Students dataStudent() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new SecurityException("Authentication required");
        }
        String username = authentication.getName();
        Students student = entityManager.find(Students.class, username);
        if (student == null) {
            throw new IllegalArgumentException("Student not found for username: " + username);
        }
        return student;
    }

    @Override
    public Majors getMajors() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new SecurityException("Authentication required");
        }
        String username = authentication.getName();
        Students student = entityManager.find(Students.class, username);
        if (student == null || student.getMajor() == null) {
            throw new IllegalArgumentException("Major not found for student: " + username);
        }
        return student.getMajor();
    }

    @Override
    public List<Students> getStudents() {
        return entityManager.createQuery("FROM Students s", Students.class).getResultList();
    }

    @Override
    public Students addStudents(Students students, String randomPassword) {

        Staffs staff = staffsService.getStaffs();
        students.setCampus(staff.getCampus());
        students.setMajor(staff.getMajorManagement());
        students.setCreator(staff);
        Students savedStudent = entityManager.merge(students);
        AccountBalances accountBalances = new AccountBalances();
        accountBalances.setBalance(0.000000);
        accountBalances.setStudent(savedStudent);
        accountBalances.setStudentId(accountBalances.getStudent().getId());
        accountBalances.setLastUpdated(LocalDateTime.now());
        accountBalancesService.createAccountBalances(accountBalances);

        try {
            String subject = "Your Student Account Information";
            emailServiceForStudentService.sendEmailToNotifyLoginInformation(students.getEmail(), subject, students, randomPassword);
        } catch (Exception e) {
            System.err.println("Failed to schedule email to " + students.getEmail() + ": " + e.getMessage());
        }
        return savedStudent;
    }

    @Override
    public long numberOfStudents() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new SecurityException("Authentication required");
        }
        String username = authentication.getName();
        Staffs staff = entityManager.find(Staffs.class, username);
        if (staff == null || staff.getMajorManagement() == null) {
            throw new IllegalArgumentException("Staff or major not found for username: " + username);
        }

        return (Long) entityManager.createQuery(
                        "SELECT COUNT(s) FROM Students s WHERE s.major.id = :staffmajor")
                .setParameter("staffmajor", staff.getMajorManagement().getMajorId())
                .getSingleResult();
    }

    @Override
    public void deleteStudent(String id) {
        Students student = entityManager.find(Students.class, id);
        if (student == null) {
            throw new IllegalArgumentException("Student with ID " + id + " not found");
        }
        entityManager.remove(student);
    }

    @Override
    public void updateStudent(String id, Students student) throws MessagingException {
        if (student == null || id == null) {
            throw new IllegalArgumentException("Student object or ID cannot be null");
        }

        Students existingStudent = entityManager.find(Students.class, id);
        if (existingStudent == null) {
            throw new IllegalArgumentException("Student with ID " + id + " not found");
        }

        updateStudentFields(existingStudent, student);
        entityManager.merge(existingStudent);

        String subject = "Your student account information after being edited";
        emailServiceForStudentService.sendEmailToNotifyInformationAfterEditing(existingStudent.getEmail(), subject, existingStudent);
    }

    @Override
    public Students getStudentById(String id) {
        return entityManager.find(Students.class, id);
    }

    @Override
    public List<Students> getPaginatedStudents(int firstResult, int pageSize) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
            throw new SecurityException("Authentication required");
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        Persons user = entityManager.find(Persons.class, username);
        if (user == null || !(user instanceof Staffs)) {
            throw new SecurityException("Only staff members can access paginated students.");
        }
        Staffs staff = (Staffs) user;
        Majors majors = staff.getMajorManagement();

        return entityManager.createQuery(
                        "SELECT s FROM Students s WHERE s.major = :staffmajor", Students.class)
                .setParameter("staffmajor", majors)
                .setFirstResult(firstResult)
                .setMaxResults(pageSize)
                .getResultList();
    }

    private void updateStudentFields(Students existing, Students updated) {
        if (updated.getFirstName() != null) existing.setFirstName(updated.getFirstName());
        if (updated.getLastName() != null) existing.setLastName(updated.getLastName());
        if (updated.getEmail() != null) existing.setEmail(updated.getEmail());
        if (updated.getPhoneNumber() != null) existing.setPhoneNumber(updated.getPhoneNumber());
        if (updated.getBirthDate() != null) existing.setBirthDate(updated.getBirthDate());
        if (updated.getGender() != null) existing.setGender(updated.getGender());
        if (updated.getFaceData() != null) existing.setFaceData(updated.getFaceData());
        if (updated.getVoiceData() != null) existing.setVoiceData(updated.getVoiceData());
        if (updated.getCountry() != null) existing.setCountry(updated.getCountry());
        if (updated.getProvince() != null) existing.setProvince(updated.getProvince());
        if (updated.getCity() != null) existing.setCity(updated.getCity());
        if (updated.getDistrict() != null) existing.setDistrict(updated.getDistrict());
        if (updated.getWard() != null) existing.setWard(updated.getWard());
        if (updated.getStreet() != null) existing.setStreet(updated.getStreet());
        if (updated.getPostalCode() != null) existing.setPostalCode(updated.getPostalCode());
        if (updated.getAvatar() != null) existing.setAvatar(updated.getAvatar());
        if (updated.getMisId() != null) existing.setMisId(updated.getMisId());
        if (updated.getCampus() != null) existing.setCampus(updated.getCampus());
        if (updated.getCreator() != null) existing.setCreator(updated.getCreator());
        if (updated.getPassword() != null && !updated.getPassword().isEmpty()) existing.setPassword(updated.getPassword());
    }
}