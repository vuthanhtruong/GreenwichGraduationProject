package com.example.demo.specializationSyllabus.dao;

import com.example.demo.specializationSyllabus.model.SpecializationSyllabuses;
import com.example.demo.specializedSubject.model.SpecializedSubject;
import com.example.demo.staff.service.StaffsService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional
public class SpecializationSyllabusesDAOImpl implements SpecializationSyllabusesDAO {


    @Override
    public void deleteSyllabus(SpecializationSyllabuses syllabus) {
        entityManager.remove(entityManager.contains(syllabus) ? syllabus : entityManager.merge(syllabus));
    }

    @PersistenceContext
    private EntityManager entityManager;

    private final StaffsService staffsService;

    public SpecializationSyllabusesDAOImpl(StaffsService staffsService) {
        this.staffsService = staffsService;
    }

    @Override
    public void addSyllabus(SpecializationSyllabuses syllabus) {
        syllabus.setCreator(staffsService.getStaff());
        entityManager.persist(syllabus);
    }

    @Override
    public SpecializationSyllabuses getSyllabusById(String syllabusId) {
        return entityManager.find(SpecializationSyllabuses.class, syllabusId);
    }

    @Override
    public List<SpecializationSyllabuses> getSyllabusesBySubject(SpecializedSubject subject) {
        return entityManager.createQuery("select s FROM SpecializationSyllabuses s where s.specializedSubject = :subject", SpecializationSyllabuses.class)
                .setParameter("subject", subject)
                .getResultList();
    }

    @Override
    public void deleteSyllabusBySubject(SpecializedSubject subject) {
        List<SpecializationSyllabuses> syllabusesList = entityManager.createQuery("select s from SpecializationSyllabuses s where s.specializedSubject = :subject", SpecializationSyllabuses.class)
                .setParameter("subject", subject)
                .getResultList();
        for (SpecializationSyllabuses syllabus : syllabusesList) {
            entityManager.remove(syllabus);
        }
    }

    @Override
    public List<SpecializationSyllabuses> getPaginatedSyllabuses(String subjectId, int firstResult, int pageSize) {
        return entityManager.createQuery("select s from SpecializationSyllabuses s where s.specializedSubject.subjectId = :subjectId", SpecializationSyllabuses.class)
                .setParameter("subjectId", subjectId)
                .setFirstResult(firstResult)
                .setMaxResults(pageSize)
                .getResultList();
    }

    @Override
    public Long numberOfSyllabuses(String subjectId) {
        return entityManager.createQuery("select count(s) from SpecializationSyllabuses s where s.specializedSubject.subjectId = :subjectId", Long.class)
                .setParameter("subjectId", subjectId)
                .getSingleResult();
    }

    @Override
    public boolean existsBySyllabusNameAndSubject(String syllabusName, String subjectId) {
        Long count = entityManager.createQuery("select count(s) from SpecializationSyllabuses s where s.syllabusName = :syllabusName and s.specializedSubject.subjectId = :subjectId", Long.class)
                .setParameter("syllabusName", syllabusName)
                .setParameter("subjectId", subjectId)
                .getSingleResult();
        return count > 0;
    }

    @Override
    public List<String> validateSyllabus(SpecializationSyllabuses syllabus, MultipartFile file, String subjectId) {
        List<String> errors = new ArrayList<>();

        // Validate syllabus name
        if (syllabus.getSyllabusName() == null || syllabus.getSyllabusName().trim().isEmpty()) {
            errors.add("Syllabus name is required");
        } else if (syllabus.getSyllabusName().length() > 255) {
            errors.add("Syllabus name must not exceed 255 characters");
        } else if (existsBySyllabusNameAndSubject(syllabus.getSyllabusName(), subjectId)) {
            errors.add("Syllabus name already exists for this subject");
        }

        // Validate file
        if (file != null && !file.isEmpty()) {
            String contentType = file.getContentType();
            List<String> allowedTypes = List.of(
                    "application/pdf",
                    "application/msword",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                    "text/plain",
                    "application/vnd.ms-powerpoint",
                    "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                    "application/zip"
            );
            if (!allowedTypes.contains(contentType)) {
                errors.add("Invalid file type. Allowed types: PDF, DOC, DOCX, TXT, PPT, PPTX, ZIP");
            }
            if (file.getSize() > 10 * 1024 * 1024) { // 10MB limit
                errors.add("File size exceeds 10MB limit");
            }
        }

        // Validate subjectId
        if (subjectId == null || subjectId.trim().isEmpty()) {
            errors.add("Subject ID is required");
        }

        return errors;
    }

}