package com.example.demo.dao.impl;

import com.example.demo.dao.StaffsDAO;
import com.example.demo.entity.*;
import com.example.demo.service.EmailServiceForStudent;
import com.example.demo.service.EmailServiceForLecture;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class StaffsDAOImpl implements StaffsDAO {
    @Override
    public List<Lecturers> getPaginatedLecturers(int firstResult, int pageSize) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        System.out.println("Username: " + username);
        // Tìm thực thể Persons dựa trên username
        Persons user = entityManager.find(Persons.class, username);
        Staffs staff = (Staffs) user;
        Majors majors=staff.getMajorManagement();

        // Truy vấn danh sách sinh viên theo major của staff
        List<Lecturers> result = entityManager.createQuery(
                        "SELECT s FROM Lecturers s WHERE s.majorManagement = :staffmajor", Lecturers.class)
                .setParameter("staffmajor", majors)
                .setFirstResult(firstResult)
                .setMaxResults(pageSize)
                .getResultList();
        return result;
    }

    private final JavaMailSenderImpl mailSender;
    private final EmailServiceForStudent emailServiceForStudent;
    private final EmailServiceForLecture emailServiceForTeacher;

    public StaffsDAOImpl(JavaMailSenderImpl mailSender, EmailServiceForStudent emailServiceForStudent, EmailServiceForLecture emailServiceForTeacher) {
        this.mailSender = mailSender;
        this.emailServiceForStudent = emailServiceForStudent;
        this.emailServiceForTeacher = emailServiceForTeacher;
    }

    @Override
    public List<Students> getPaginatedStudents(int firstResult, int pageSize) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        System.out.println("Username: " + username);
        // Tìm thực thể Persons dựa trên username
        Persons user = entityManager.find(Persons.class, username);
        Staffs staff = (Staffs) user;
        Majors majors=staff.getMajorManagement();

        // Truy vấn danh sách sinh viên theo major của staff
        List<Students> result = entityManager.createQuery(
                        "SELECT s FROM Students s WHERE s.major = :staffmajor", Students.class)
                .setParameter("staffmajor", majors)
                .setFirstResult(firstResult)
                .setMaxResults(pageSize)
                .getResultList();
        return result;
    }

    @Override
    public boolean existsByPhoneNumberExcludingId(String phoneNumber, String id) {
        Query query = entityManager.createQuery(
                "SELECT COUNT(s) > 0 FROM Persons s WHERE s.phoneNumber = :phoneNumber AND s.id != :id"
        );
        query.setParameter("phoneNumber", phoneNumber);
        query.setParameter("id", id);
        return (boolean) query.getSingleResult();
    }

    @Override
    public boolean existsByEmailExcludingId(String email, String id) {
        Query query = entityManager.createQuery(
                "SELECT COUNT(s) > 0 FROM Persons s WHERE s.email = :email AND s.id != :id"
        );
        query.setParameter("email", email);
        query.setParameter("id", id);
        return (boolean) query.getSingleResult();
    }

    @Override
    public Students getStudentById(String id) {
        Students student=entityManager.find(Students.class, id);
        return student;
    }

    @Override
    public Lecturers getLecturerById(String id) {
        Lecturers lecturer=entityManager.find(Lecturers.class, id);
        return lecturer;
    }

    @Override
    public void updateLecturer(String id, Lecturers lecturer) throws MessagingException{
        if (lecturer == null) {
            throw new IllegalArgumentException("Student object cannot be null");
        }

        Lecturers existingLecturers = entityManager.find(Lecturers.class, id);
        if (existingLecturers == null) {
            throw new IllegalArgumentException("Student with ID " + id + " not found");
        }

        // Validate required fields
        if (lecturer.getEmail() == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }
        if (lecturer.getPhoneNumber() == null) {
            throw new IllegalArgumentException("Phone number cannot be null");
        }

        // Update fields from Persons (only if non-null)
        if (lecturer.getFirstName() != null) {
            existingLecturers.setFirstName(lecturer.getFirstName());
        }
        if (lecturer.getLastName() != null) {
            existingLecturers.setLastName(lecturer.getLastName());
        }
        existingLecturers.setEmail(lecturer.getEmail()); // Required field
        existingLecturers.setPhoneNumber(lecturer.getPhoneNumber()); // Required field
        if (lecturer.getBirthDate() != null) {
            existingLecturers.setBirthDate(lecturer.getBirthDate());
        }
        if (lecturer.getGender() != null) {
            existingLecturers.setGender(lecturer.getGender());
        }
        if (lecturer.getFaceData() != null) {
            existingLecturers.setFaceData(lecturer.getFaceData());
        }
        if (lecturer.getVoiceData() != null) {
            existingLecturers.setVoiceData(lecturer.getVoiceData());
        }
        if (lecturer.getCountry() != null) {
            existingLecturers.setCountry(lecturer.getCountry());
        }
        if (lecturer.getProvince() != null) {
            existingLecturers.setProvince(lecturer.getProvince());
        }
        if (lecturer.getCity() != null) {
            existingLecturers.setCity(lecturer.getCity());
        }
        if (lecturer.getDistrict() != null) {
            existingLecturers.setDistrict(lecturer.getDistrict());
        }
        if (lecturer.getWard() != null) {
            existingLecturers.setWard(lecturer.getWard());
        }
        if (lecturer.getStreet() != null) {
            existingLecturers.setStreet(lecturer.getStreet());
        }
        if (lecturer.getPostalCode() != null) {
            existingLecturers.setPostalCode(lecturer.getPostalCode());
        }
        if (lecturer.getCampus() != null) {
            existingLecturers.setCampus(lecturer.getCampus());
        }
        if (lecturer.getMajorManagement() != null) {
            existingLecturers.setMajorManagement(lecturer.getMajorManagement());
        }
        if (lecturer.getCreator() != null) {
            existingLecturers.setCreator(lecturer.getCreator());
        }
        if (lecturer.getPassword() != null && !lecturer.getPassword().isEmpty()) {
            existingLecturers.setPassword(lecturer.getPassword());
        }
        entityManager.merge(existingLecturers);
        String subject = "Your student account information after being edited";
        emailServiceForTeacher.sendEmailToNotifyInformationAfterEditing(existingLecturers.getEmail(), subject, existingLecturers);
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
        entityManager.merge(existingStudent);
        String subject = "Your student account information after being edited";
        emailServiceForStudent.sendEmailToNotifyInformationAfterEditing(existingStudent.getEmail(), subject, existingStudent);
    }

    @Override
    public void deleteStudent(String id) {
        Students student=entityManager.find(Students.class, id);
        entityManager.remove(student);
    }

    @Override
    public void deleteLecturer(String id) {
        Lecturers lecturer=entityManager.find(Lecturers.class, id);
        entityManager.remove(lecturer);
    }

    @Override
    public boolean existsPersonById(String id) {
        Query query = entityManager.createQuery(
                "SELECT COUNT(p) FROM Persons p WHERE p.id = :id");
        query.setParameter("id", id);
        return (Long) query.getSingleResult() > 0;
    }

    @Override
    public boolean existsByEmail(String email) {
        Query query = entityManager.createQuery(
                "SELECT COUNT(p) FROM Persons p WHERE p.email = :email");
        query.setParameter("email", email);
        return (Long) query.getSingleResult() > 0;
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        Query query = entityManager.createQuery(
                "SELECT COUNT(p) FROM Persons p WHERE p.phoneNumber = :phoneNumber");
        query.setParameter("phoneNumber", phoneNumber);
        return (Long) query.getSingleResult() > 0;
    }
    @Override
    public Majors getMajors() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Staffs staff = entityManager.find(Staffs.class, username);
        return staff.getMajorManagement();
    }

    @Override
    public long numberOfStudents() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Staffs staff = entityManager.find(Staffs.class, username);

        Long totalStudents = (Long) entityManager.createQuery(
                        "SELECT COUNT(s) FROM Students s WHERE s.major.id = :staffmajor")
                .setParameter("staffmajor", staff.getMajorManagement().getMajorId())
                .getSingleResult();
        return totalStudents;
    }

    @Override
    public long numberOfLecturers() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Staffs staff = entityManager.find(Staffs.class, username);

        Long totalLecturers = (Long) entityManager.createQuery(
                        "SELECT COUNT(l) FROM Lecturers l WHERE l.majorManagement.id = :staffmajor")
                .setParameter("staffmajor", staff.getMajorManagement().getMajorId())
                .getSingleResult();
        return totalLecturers;
    }

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Staffs getStaffs() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("No authenticated user found.");
        }

        Object principal = authentication.getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        System.out.println("Username: " + username);

        Persons user = entityManager.find(Persons.class, username);
        if (user == null) {
            throw new IllegalArgumentException("User not found with username: " + username);
        }
        if (!(user instanceof Staffs)) {
            throw new SecurityException("User is not a staff member. Entity type: " + user.getClass().getSimpleName());
        }
        return (Staffs) user;
    }

    @Override
    public Lecturers addLecturers(Lecturers lecturers,String randomPassword) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();

        Persons user = entityManager.find(Persons.class, username);
        if (!(user instanceof Staffs)) {
            throw new SecurityException("Only staff members can add students.");
        }
        Staffs staff = (Staffs) user;
        lecturers.setCampus(staff.getCampus());
        lecturers.setMajorManagement(staff.getMajorManagement());
        lecturers.setCreator(staff);

        String rawPassword = randomPassword;
        Lecturers savedStudent = entityManager.merge(lecturers);

        try {
            String subject = "Your Student Account Information";
            emailServiceForTeacher.sendEmailToNotifyLoginInformation(lecturers.getEmail(), subject, lecturers, rawPassword);
        } catch (Exception e) {
            System.err.println("Failed to schedule email to " + lecturers.getEmail() + ": " + e.getMessage());
        }
        return savedStudent;
    }

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
            emailServiceForStudent.sendEmailToNotifyLoginInformation(students.getEmail(), subject, students, rawPassword);
        } catch (Exception e) {
            System.err.println("Failed to schedule email to " + students.getEmail() + ": " + e.getMessage());
        }
        return savedStudent;
    }

    @Override
    public List<Students> getAll() {
        List<Students> students=entityManager.createQuery("from Students s", Students.class).getResultList();
        return  students;
    }

    @Override
    public List<Classes> getClasses() {
        return List.of();
    }

    @Override
    public List<Lecturers> getLecturers() {
        List<Lecturers> lecturers=entityManager.createQuery("from Lecturers l", Lecturers.class).getResultList();
        return lecturers;
    }

}
