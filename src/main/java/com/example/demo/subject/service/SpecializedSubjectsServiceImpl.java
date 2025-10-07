package com.example.demo.subject.service;

import com.example.demo.Specialization.model.Specialization;
import com.example.demo.subject.dao.SpecializedSubjectsDAO;
import com.example.demo.subject.model.SpecializedSubject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Service implementation for managing specialized subjects.
 * This class acts as a middle layer between controllers and DAO,
 * handling validation, transaction management, and delegation to DAO operations.
 */
@Service
@Transactional
public class SpecializedSubjectsServiceImpl implements SpecializedSubjectsService {

    private static final Logger logger = LoggerFactory.getLogger(SpecializedSubjectsServiceImpl.class);

    private final SpecializedSubjectsDAO specializedSubjectsDAO;

    public SpecializedSubjectsServiceImpl(SpecializedSubjectsDAO specializedSubjectsDAO) {
        this.specializedSubjectsDAO = specializedSubjectsDAO;
    }

    @Override
    public List<SpecializedSubject> searchSubjects(String searchType, String keyword, int firstResult, int pageSize, Specialization specialization) {
        logger.debug("Searching specialized subjects: type={}, keyword={}, specialization={}", searchType, keyword,
                specialization != null ? specialization.getSpecializationId() : null);
        return specializedSubjectsDAO.searchSubjects(searchType, keyword, firstResult, pageSize, specialization);
    }

    @Override
    public long countSearchResults(String searchType, String keyword, Specialization specialization) {
        logger.debug("Counting specialized subjects for searchType={}, keyword={}, specialization={}", searchType, keyword,
                specialization != null ? specialization.getSpecializationId() : null);
        return specializedSubjectsDAO.countSearchResults(searchType, keyword, specialization);
    }

    @Override
    public boolean existsBySubjectExcludingName(String subjectName, String subjectId) {
        logger.debug("Checking if subject name '{}' exists excluding ID '{}'", subjectName, subjectId);
        return specializedSubjectsDAO.existsBySubjectExcludingName(subjectName, subjectId);
    }

    @Override
    public void addSubject(SpecializedSubject subject, Specialization specialization) {
        if (subject == null) {
            throw new IllegalArgumentException("Subject cannot be null");
        }
        if (specialization == null) {
            throw new IllegalArgumentException("Specialization cannot be null");
        }
        subject.setSpecialization(specialization);
        logger.info("Adding new specialized subject: name='{}', specialization='{}'",
                subject.getSubjectName(), specialization.getSpecializationName());
        specializedSubjectsDAO.addSubject(subject,specialization);
    }

    @Override
    public SpecializedSubject getSubjectById(String subjectId) {
        logger.debug("Retrieving specialized subject by ID: {}", subjectId);
        return specializedSubjectsDAO.getSubjectById(subjectId);
    }

    @Override
    public SpecializedSubject getSubjectByName(String subjectName) {
        logger.debug("Retrieving specialized subject by name: {}", subjectName);
        return specializedSubjectsDAO.getSubjectByName(subjectName);
    }

    @Override
    public SpecializedSubject checkNameSubject(SpecializedSubject subject) {
        logger.debug("Checking existence of specialized subject by name");
        return specializedSubjectsDAO.checkNameSubject(subject);
    }

    @Override
    public List<SpecializedSubject> subjectsBySpecialization(Specialization specialization) {
        logger.debug("Retrieving specialized subjects for specialization ID: {}",
                specialization != null ? specialization.getSpecializationId() : null);
        return specializedSubjectsDAO.subjectsBySpecialization(specialization);
    }

    @Override
    public List<SpecializedSubject> AcceptedSubjectsBySpecialization(Specialization specialization) {
        logger.debug("Retrieving accepted subjects for specialization ID: {}",
                specialization != null ? specialization.getSpecializationId() : null);
        return specializedSubjectsDAO.AcceptedSubjectsBySpecialization(specialization);
    }

    @Override
    public List<SpecializedSubject> getSubjects() {
        logger.debug("Retrieving all specialized subjects");
        return specializedSubjectsDAO.getSubjects();
    }

    @Override
    public SpecializedSubject editSubject(String id, SpecializedSubject subject) {
        logger.info("Editing specialized subject with ID: {}", id);
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Subject ID cannot be null or empty");
        }
        if (subject == null) {
            throw new IllegalArgumentException("Subject cannot be null");
        }
        return specializedSubjectsDAO.editSubject(id, subject);
    }

    @Override
    public void deleteSubject(String id) {
        logger.warn("Deleting specialized subject with ID: {}", id);
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Subject ID cannot be null or empty");
        }
        specializedSubjectsDAO.deleteSubject(id);
    }

    @Override
    public String generateUniqueSubjectId(String specializationId, LocalDate createdDate) {
        logger.debug("Generating unique subject ID for specialization: {}", specializationId);
        if (createdDate == null) {
            createdDate = LocalDate.now();
        }
        return specializedSubjectsDAO.generateUniqueSubjectId(specializationId, createdDate);
    }

    @Override
    public Map<String, String> validateSubject(SpecializedSubject subject) {
        logger.debug("Validating specialized subject: {}", subject != null ? subject.getSubjectName() : "null");
        return specializedSubjectsDAO.validateSubject(subject);
    }

    @Override
    public List<SpecializedSubject> getPaginatedSubjects(int firstResult, int pageSize, Specialization specialization) {
        logger.debug("Retrieving paginated subjects: start={}, size={}, specialization={}",
                firstResult, pageSize, specialization != null ? specialization.getSpecializationId() : null);
        return specializedSubjectsDAO.getPaginatedSubjects(firstResult, pageSize, specialization);
    }

    @Override
    public long numberOfSubjects(Specialization specialization) {
        logger.debug("Counting number of subjects for specialization ID: {}",
                specialization != null ? specialization.getSpecializationId() : null);
        return specializedSubjectsDAO.numberOfSubjects(specialization);
    }
}
