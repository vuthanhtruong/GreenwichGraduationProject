package com.example.demo.dao.impl;

import com.example.demo.dao.LecturesDAO;
import com.example.demo.entity.Lecturers;
import com.example.demo.entity.Majors;
import com.example.demo.entity.Persons;
import com.example.demo.entity.Staffs;
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

import java.util.List;

@Repository
@Transactional
public class LecturesDAOImpl implements LecturesDAO {

    private final StaffsService staffsService;
    @PersistenceContext
    private EntityManager entityManager;

    private final EmailServiceForLectureService emailServiceForLectureService;
    private final EmailServiceForStudentService emailServiceForStudentService;

    public LecturesDAOImpl(EmailServiceForLectureService emailServiceForLectureService,
                           EmailServiceForStudentService emailServiceForStudentService, StaffsService staffsService) {
        if (emailServiceForLectureService == null || emailServiceForStudentService == null) {
            throw new IllegalArgumentException("Email services cannot be null");
        }
        this.emailServiceForLectureService = emailServiceForLectureService;
        this.emailServiceForStudentService = emailServiceForStudentService;
        this.staffsService = staffsService;
    }

    @Override
    public List<Lecturers> getLecturers() {
        return entityManager.createQuery("FROM Lecturers l", Lecturers.class).getResultList();
    }

    @Override
    public Lecturers addLecturers(Lecturers lecturers, String randomPassword) {

        Staffs staff = staffsService.getStaff();
        lecturers.setCampus(staff.getCampus());
        lecturers.setMajorManagement(staff.getMajorManagement());
        lecturers.setCreator(staff);

        Lecturers savedLecturer = entityManager.merge(lecturers);

        try {
            String subject = "Your Lecturer Account Information";
            emailServiceForLectureService.sendEmailToNotifyLoginInformation(lecturers.getEmail(), subject, lecturers, randomPassword);
        } catch (Exception e) {
            System.err.println("Failed to schedule email to " + lecturers.getEmail() + ": " + e.getMessage());
        }
        return savedLecturer;
    }

    @Override
    public long numberOfLecturers() {
        Staffs staff = staffsService.getStaff();
        if (staff == null) {
            throw new IllegalArgumentException("Staff not found");
        }

        return (Long) entityManager.createQuery(
                        "SELECT COUNT(l) FROM Lecturers l WHERE l.majorManagement.id = :staffmajor")
                .setParameter("staffmajor", staff.getMajorManagement().getMajorId())
                .getSingleResult();
    }

    @Override
    public void deleteLecturer(String id) {
        Lecturers lecturer = entityManager.find(Lecturers.class, id);
        if (lecturer == null) {
            throw new IllegalArgumentException("Lecturer with ID " + id + " not found");
        }
        entityManager.remove(lecturer);
    }

    @Override
    public void updateLecturer(String id, Lecturers lecturer) throws MessagingException {
        if (lecturer == null || id == null) {
            throw new IllegalArgumentException("Lecturer object or ID cannot be null");
        }

        Lecturers existingLecturer = entityManager.find(Lecturers.class, id);
        if (existingLecturer == null) {
            throw new IllegalArgumentException("Lecturer with ID " + id + " not found");
        }

        validateLecturer(lecturer);

        updateLecturerFields(existingLecturer, lecturer);
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

        Staffs staff = staffsService.getStaff();
        Majors majors = staff.getMajorManagement();

        return entityManager.createQuery(
                        "SELECT s FROM Lecturers s WHERE s.majorManagement = :staffmajor", Lecturers.class)
                .setParameter("staffmajor", majors)
                .setFirstResult(firstResult)
                .setMaxResults(pageSize)
                .getResultList();
    }

    private void validateLecturer(Lecturers lecturer) {
        if (lecturer.getEmail() == null || lecturer.getPhoneNumber() == null) {
            throw new IllegalArgumentException("Email and phone number are required");
        }
    }

    private void updateLecturerFields(Lecturers existing, Lecturers updated) {
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
        if (updated.getCampus() != null) existing.setCampus(updated.getCampus());
        if (updated.getCreator() != null) existing.setCreator(updated.getCreator());
        if (updated.getPassword() != null && !updated.getPassword().isEmpty()) existing.setPassword(updated.getPassword());
        if (updated.getAvatar() != null) existing.setAvatar(updated.getAvatar());
    }
}