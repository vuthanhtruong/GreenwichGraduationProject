package com.example.demo.dao.impl;

import com.example.demo.dao.LecturesDAO;
import com.example.demo.entity.Lecturers;
import com.example.demo.entity.Majors;
import com.example.demo.entity.Persons;
import com.example.demo.entity.Staffs;
import com.example.demo.service.EmailServiceForLectureService;
import com.example.demo.service.EmailServiceForStudentService;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class LecturesDAOImpl implements LecturesDAO {

    private final JavaMailSenderImpl mailSender;
    private final EmailServiceForStudentService emailServiceForStudentService;
    private final EmailServiceForLectureService emailServiceForLectureService;


    public LecturesDAOImpl(JavaMailSenderImpl mailSender, EmailServiceForStudentService emailServiceForStudentService, EmailServiceForLectureService emailServiceForLectureService) {
        this.mailSender = mailSender;
        this.emailServiceForStudentService = emailServiceForStudentService;
        this.emailServiceForLectureService = emailServiceForLectureService;
    }

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Lecturers> getLecturers() {
        return entityManager.createQuery("from Lecturers l", Lecturers.class).getResultList();
    }

    @Override
    public Lecturers addLecturers(Lecturers lecturers, String randomPassword) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();

        Persons user = entityManager.find(Persons.class, username);
        if (!(user instanceof Staffs)) {
            throw new SecurityException("Only staff members can add lecturers.");
        }
        Staffs staff = (Staffs) user;
        lecturers.setCampus(staff.getCampus());
        lecturers.setMajorManagement(staff.getMajorManagement());
        lecturers.setCreator(staff);

        String rawPassword = randomPassword;
        Lecturers savedLecturer = entityManager.merge(lecturers);

        try {
            String subject = "Your Lecturer Account Information";
            emailServiceForLectureService.sendEmailToNotifyLoginInformation(lecturers.getEmail(), subject, lecturers, rawPassword);
        } catch (Exception e) {
            System.err.println("Failed to schedule email to " + lecturers.getEmail() + ": " + e.getMessage());
        }
        return savedLecturer;
    }

    @Override
    public long numberOfLecturers() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Staffs staff = entityManager.find(Staffs.class, username);

        return (Long) entityManager.createQuery(
                        "SELECT COUNT(l) FROM Lecturers l WHERE l.majorManagement.id = :staffmajor")
                .setParameter("staffmajor", staff.getMajorManagement().getMajorId())
                .getSingleResult();
    }

    @Override
    public void deleteLecturer(String id) {
        Lecturers lecturer = entityManager.find(Lecturers.class, id);
        entityManager.remove(lecturer);
    }

    @Override
    public void updateLecturer(String id, Lecturers lecturer) throws MessagingException {
        if (lecturer == null) {
            throw new IllegalArgumentException("Lecturer object cannot be null");
        }

        Lecturers existingLecturer = entityManager.find(Lecturers.class, id);
        if (existingLecturer == null) {
            throw new IllegalArgumentException("Lecturer with ID " + id + " not found");
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
            existingLecturer.setFirstName(lecturer.getFirstName());
        }
        if (lecturer.getLastName() != null) {
            existingLecturer.setLastName(lecturer.getLastName());
        }
        if(lecturer.getEmail() != null) {
            existingLecturer.setEmail(lecturer.getEmail());
        }
        if(lecturer.getPhoneNumber() != null) {
            existingLecturer.setPhoneNumber(lecturer.getPhoneNumber());
        }
        if (lecturer.getBirthDate() != null) {
            existingLecturer.setBirthDate(lecturer.getBirthDate());
        }
        if (lecturer.getGender() != null) {
            existingLecturer.setGender(lecturer.getGender());
        }
        if (lecturer.getFaceData() != null) {
            existingLecturer.setFaceData(lecturer.getFaceData());
        }
        if (lecturer.getVoiceData() != null) {
            existingLecturer.setVoiceData(lecturer.getVoiceData());
        }
        if (lecturer.getCountry() != null) {
            existingLecturer.setCountry(lecturer.getCountry());
        }
        if (lecturer.getProvince() != null) {
            existingLecturer.setProvince(lecturer.getProvince());
        }
        if (lecturer.getCity() != null) {
            existingLecturer.setCity(lecturer.getCity());
        }
        if (lecturer.getDistrict() != null) {
            existingLecturer.setDistrict(lecturer.getDistrict());
        }
        if (lecturer.getWard() != null) {
            existingLecturer.setWard(lecturer.getWard());
        }
        if (lecturer.getStreet() != null) {
            existingLecturer.setStreet(lecturer.getStreet());
        }
        if (lecturer.getPostalCode() != null) {
            existingLecturer.setPostalCode(lecturer.getPostalCode());
        }
        if (lecturer.getCampus() != null) {
            existingLecturer.setCampus(lecturer.getCampus());
        }
        if (lecturer.getMajorManagement() != null) {
            existingLecturer.setMajorManagement(lecturer.getMajorManagement());
        }
        if (lecturer.getCreator() != null) {
            existingLecturer.setCreator(lecturer.getCreator());
        }
        if (lecturer.getPassword() != null && !lecturer.getPassword().isEmpty()) {
            existingLecturer.setPassword(lecturer.getPassword());
        }
        if (lecturer.getAvatar() != null) {
            existingLecturer.setAvatar(lecturer.getAvatar());
        }
        entityManager.merge(existingLecturer);
        String subject = "Your lecturer account information after being edited";
        emailServiceForLectureService.sendEmailToNotifyInformationAfterEditing(existingLecturer.getEmail(), subject, existingLecturer);
    }

    @Override
    public Lecturers getLecturerById(String id) {
        return entityManager.find(Lecturers.class, id);
    }

    @Override
    public List<Lecturers> getPaginatedLecturers(int firstResult, int pageSize) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        Persons user = entityManager.find(Persons.class, username);
        Staffs staff = (Staffs) user;
        Majors majors = staff.getMajorManagement();

        return entityManager.createQuery(
                        "SELECT s FROM Lecturers s WHERE s.majorManagement = :staffmajor", Lecturers.class)
                .setParameter("staffmajor", majors)
                .setFirstResult(firstResult)
                .setMaxResults(pageSize)
                .getResultList();
    }
}