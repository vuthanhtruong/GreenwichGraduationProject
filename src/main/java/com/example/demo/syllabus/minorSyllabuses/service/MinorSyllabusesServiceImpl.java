// File: MinorSyllabusesServiceImpl.java
package com.example.demo.syllabus.minorSyllabuses.service;

import com.example.demo.subject.minorSubject.model.MinorSubjects;
import com.example.demo.syllabus.minorSyllabuses.dao.MinorSyllabusesDAO;
import com.example.demo.syllabus.minorSyllabuses.model.MinorSyllabuses;
import jakarta.validation.Validator;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class MinorSyllabusesServiceImpl implements MinorSyllabusesService {

    private final MinorSyllabusesDAO dao;
    private final Validator validator;

    public MinorSyllabusesServiceImpl(MinorSyllabusesDAO dao, Validator validator) {
        this.dao = dao;
        this.validator = validator;
    }

    @Override
    public void addSyllabus(MinorSyllabuses syllabus) { dao.addSyllabus(syllabus); }

    @Override
    public MinorSyllabuses getSyllabusById(String syllabusId) { return dao.getSyllabusById(syllabusId); }

    @Override
    public List<MinorSyllabuses> getSyllabusesBySubject(MinorSubjects subject) { return dao.getSyllabusesBySubject(subject); }

    @Override
    public void deleteSyllabusBySubject(MinorSubjects subject) { dao.deleteSyllabusBySubject(subject); }

    @Override
    public List<MinorSyllabuses> getPaginatedSyllabuses(String subjectId, int firstResult, int pageSize) {
        return dao.getPaginatedSyllabuses(subjectId, firstResult, pageSize);
    }

    @Override
    public Long numberOfSyllabuses(String subjectId) { return dao.numberOfSyllabuses(subjectId); }

    @Override
    public void deleteSyllabus(MinorSyllabuses syllabus) { dao.deleteSyllabus(syllabus); }

    @Override
    public List<String> syllabusValidation(MinorSyllabuses syllabus, MultipartFile file) {
        List<String> errors = new ArrayList<>();
        validator.validate(syllabus).forEach(v -> errors.add(v.getMessage()));

        if (file == null || file.isEmpty()) {
            errors.add("Please select a file to upload.");
        } else {
            Set<String> allowed = Set.of(
                    "application/pdf", "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                    "text/plain", "application/vnd.ms-powerpoint", "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                    "application/zip", "application/x-zip-compressed", "application/octet-stream"
            );
            if (!allowed.contains(file.getContentType())) {
                errors.add("Only PDF, DOC, DOCX, TXT, PPT, PPTX, ZIP allowed.");
            }
        }

        if (syllabus.getSyllabusName() == null || syllabus.getSyllabusName().trim().isEmpty()) {
            errors.add("Syllabus name cannot be blank.");
        } else if (!syllabus.getSyllabusName().matches("^[\\p{L}\\p{N}][\\p{L}\\p{N} .'-]{1,99}$")) {
            errors.add("Invalid syllabus name format.");
        }

        return errors;
    }
}