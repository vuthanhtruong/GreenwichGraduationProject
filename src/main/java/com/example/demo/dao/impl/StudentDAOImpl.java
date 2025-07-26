package com.example.demo.dao.impl;

import com.example.demo.dao.StudentsDAO;
import com.example.demo.entity.*;
import com.example.demo.service.EmailServiceForLectureService;
import com.example.demo.service.EmailServiceForStudentService;
import com.example.demo.service.StudentsService;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional
public class StudentDAOImpl implements StudentsDAO {

    @Override
    public Students dataStudent() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Students students = entityManager.find(Students.class, username);
        return students;
    }

    @Override
    public Majors getMajors() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Students students = entityManager.find(Students.class, username);
        return students.getMajor();
    }

    private final JavaMailSenderImpl mailSender;
    private final EmailServiceForStudentService  emailServiceForStudentService;
    private final EmailServiceForLectureService emailServiceForLectureService;

    public StudentDAOImpl(JavaMailSenderImpl mailSender, EmailServiceForLectureService emailService, EmailServiceForStudentService emailServiceForStudentService, EmailServiceForLectureService emailServiceForLectureService) {
        this.mailSender = mailSender;
        this.emailServiceForStudentService = emailServiceForStudentService;
        this.emailServiceForLectureService = emailServiceForLectureService;
    }

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Students> getStudents() {
        return entityManager.createQuery("from Students s", Students.class).getResultList();
    }

    @Override
    public Students addStudents(Students students, String randomPassword) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();

        Persons user = entityManager.find(Persons.class, username);
        if (!(user instanceof Staffs)) {
            throw new SecurityException("Only staff members can add students.");
        }
        Staffs staff = (Staffs) user;
        students.setCampus(staff.getCampus());
        students.setMajor(staff.getMajorManagement());
        students.setCreator(staff);

        String rawPassword = randomPassword;
        Students savedStudent = entityManager.merge(students);

        try {
            String subject = "Your Student Account Information";
            emailServiceForStudentService.sendEmailToNotifyLoginInformation(students.getEmail(), subject, students, rawPassword);
        } catch (Exception e) {
            System.err.println("Failed to schedule email to " + students.getEmail() + ": " + e.getMessage());
        }
        return savedStudent;
    }

    @Override
    public long numberOfStudents() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Staffs staff = entityManager.find(Staffs.class, username);

        return (Long) entityManager.createQuery(
                        "SELECT COUNT(s) FROM Students s WHERE s.major.id = :staffmajor")
                .setParameter("staffmajor", staff.getMajorManagement().getMajorId())
                .getSingleResult();
    }

    @Override
    public void deleteStudent(String id) {
        Students student = entityManager.find(Students.class, id);
        entityManager.remove(student);
    }

    @Override
    public void updateStudent(String id, Students student) throws MessagingException {
        if (student == null) {
            throw new IllegalArgumentException("Student object cannot be null");
        }

        Students existingStudent = entityManager.find(Students.class, id);
        if (existingStudent == null) {
            throw new IllegalArgumentException("Student with ID " + id + " not found");
        }

        // Validate required fields
        if (student.getEmail() == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }
        if (student.getPhoneNumber() == null) {
            throw new IllegalArgumentException("Phone number cannot be null");
        }

        // Update fields from Persons (only if non-null)
        if (student.getFirstName() != null) {
            existingStudent.setFirstName(student.getFirstName());
        }
        if (student.getLastName() != null) {
            existingStudent.setLastName(student.getLastName());
        }
        existingStudent.setEmail(student.getEmail()); // Required field
        existingStudent.setPhoneNumber(student.getPhoneNumber()); // Required field
        if (student.getBirthDate() != null) {
            existingStudent.setBirthDate(student.getBirthDate());
        }
        if (student.getGender() != null) {
            existingStudent.setGender(student.getGender());
        }
        if (student.getFaceData() != null) {
            existingStudent.setFaceData(student.getFaceData());
        }
        if (student.getVoiceData() != null) {
            existingStudent.setVoiceData(student.getVoiceData());
        }
        if (student.getCountry() != null) {
            existingStudent.setCountry(student.getCountry());
        }
        if (student.getProvince() != null) {
            existingStudent.setProvince(student.getProvince());
        }
        if (student.getCity() != null) {
            existingStudent.setCity(student.getCity());
        }
        if (student.getDistrict() != null) {
            existingStudent.setDistrict(student.getDistrict());
        }
        if (student.getWard() != null) {
            existingStudent.setWard(student.getWard());
        }
        if (student.getStreet() != null) {
            existingStudent.setStreet(student.getStreet());
        }
        if (student.getPostalCode() != null) {
            existingStudent.setPostalCode(student.getPostalCode());
        }

        // Update Students-specific fields (only if non-null)
        if (student.getMisId() != null) {
            existingStudent.setMisId(student.getMisId());
        }
        if (student.getCampus() != null) {
            existingStudent.setCampus(student.getCampus());
        }
        if (student.getMajor() != null) {
            existingStudent.setMajor(student.getMajor());
        }
        if (student.getCreator() != null) {
            existingStudent.setCreator(student.getCreator());
        }
        if (student.getPassword() != null && !student.getPassword().isEmpty()) {
            existingStudent.setPassword(student.getPassword());
        }
        if(entityManager.merge(existingStudent)!=null) {
            String subject = "Your student account information after being edited";
            emailServiceForStudentService.sendEmailToNotifyInformationAfterEditing(existingStudent.getEmail(), subject, existingStudent);
        }
    }

    @Override
    public Students getStudentById(String id) {
        return entityManager.find(Students.class, id);
    }

    @Override
    public List<Students> getPaginatedStudents(int firstResult, int pageSize) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        Persons user = entityManager.find(Persons.class, username);
        Staffs staff = (Staffs) user;
        Majors majors = staff.getMajorManagement();

        return entityManager.createQuery(
                        "SELECT s FROM Students s WHERE s.major = :staffmajor", Students.class)
                .setParameter("staffmajor", majors)
                .setFirstResult(firstResult)
                .setMaxResults(pageSize)
                .getResultList();
    }
}