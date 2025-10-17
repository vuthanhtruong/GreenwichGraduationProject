package com.example.demo.syllabus.majorSyllabus.service;

import com.example.demo.syllabus.majorSyllabus.dao.SyllabusesDAO;
import com.example.demo.subject.majorSubject.model.MajorSubjects;
import com.example.demo.syllabus.majorSyllabus.model.MajorSyllabuses;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Validator;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class SyllabusesServiceImpl implements SyllabusesService {
    @Override
    public void deleteSyllabus(MajorSyllabuses syllabus) {
        syllabusesDAO.deleteSyllabus(syllabus);
    }

    private final SyllabusesDAO syllabusesDAO;
    private final Validator validator;

    public SyllabusesServiceImpl(SyllabusesDAO syllabusesDAO, Validator validator) {
        this.syllabusesDAO = syllabusesDAO;
        this.validator = validator;
    }

    @Override
    public void addSyllabus(MajorSyllabuses syllabus) {
        syllabusesDAO.addSyllabus(syllabus);
    }

    @Override
    public MajorSyllabuses getSyllabusById(String syllabusId) {
        return syllabusesDAO.getSyllabusById(syllabusId);
    }

    @Override
    public List<MajorSyllabuses> getSyllabusesBySubject(MajorSubjects subject) {
        return syllabusesDAO.getSyllabusesBySubject(subject);
    }

    @Override
    public void deleteSyllabusBySubject(MajorSubjects subject) {
        syllabusesDAO.deleteSyllabusBySubject(subject);
    }

    @Override
    public List<MajorSyllabuses> getPaginatedSyllabuses(String subjectId, int firstResult, int pageSize) {
        return syllabusesDAO.getPaginatedSyllabuses(subjectId, firstResult, pageSize);
    }

    @Override
    public Long numberOfSyllabuses(String subjectId) {
        return syllabusesDAO.numberOfSyllabuses(subjectId);
    }

    @Override
    public List<String> syllabusValidation(MajorSyllabuses syllabus, MultipartFile file) {
        List<String> errors = new ArrayList<>();

        // Annotation-based validation
        validator.validate(syllabus).forEach(violation -> errors.add(violation.getMessage()));

        // File validation
        if (file == null || file.isEmpty()) {
            errors.add("Please select a file to upload.");
        } else {
            Set<String> allowedTypes = Set.of(
                    "application/pdf",
                    "application/msword",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                    "text/plain",
                    "application/vnd.ms-powerpoint",
                    "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                    "application/zip",
                    "application/x-zip-compressed",
                    "application/octet-stream"
            );
            if (!allowedTypes.contains(file.getContentType())) {
                errors.add("Only PDF, DOC, DOCX, TXT, PPT, PPTX, or ZIP files are allowed.");
            }
        }

        // Syllabus name validation
        if (syllabus.getSyllabusName() == null || syllabus.getSyllabusName().trim().isEmpty()) {
            errors.add("Syllabus name cannot be blank.");
        } else if (!isValidSyllabusName(syllabus.getSyllabusName())) {
            errors.add("Syllabus name is not valid. Only letters, numbers, spaces, and standard punctuation are allowed.");
        }

        return errors;
    }

    private boolean isValidSyllabusName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        String nameRegex = "^[\\p{L}\\p{N}][\\p{L}\\p{N} .'-]{1,99}$";
        return name.matches(nameRegex);
    }
}