package com.example.demo.syllabus.specializationSyllabus.service;

import com.example.demo.syllabus.specializationSyllabus.dao.SpecializationSyllabusesDAO;
import com.example.demo.syllabus.specializationSyllabus.model.SpecializationSyllabuses;
import com.example.demo.subject.specializedSubject.model.SpecializedSubject;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class SpecializationSyllabusesServiceImpl implements SpecializationSyllabusesService{
    @Override
    public void deleteSyllabus(SpecializationSyllabuses syllabus) {
        specializationSyllabusesDAO.deleteSyllabus(syllabus);
    }

    @Override
    public boolean existsBySyllabusNameAndSubject(String syllabusName, String subjectId) {
        return specializationSyllabusesDAO.existsBySyllabusNameAndSubject(syllabusName, subjectId);
    }

    @Override
    public List<String> validateSyllabus(SpecializationSyllabuses syllabus, MultipartFile file, String subjectId) {
        return specializationSyllabusesDAO.validateSyllabus(syllabus,file,subjectId);
    }

    private final SpecializationSyllabusesDAO specializationSyllabusesDAO;

    public SpecializationSyllabusesServiceImpl(SpecializationSyllabusesDAO specializationSyllabusesDAO) {
        this.specializationSyllabusesDAO = specializationSyllabusesDAO;
    }

    @Override
    public void addSyllabus(SpecializationSyllabuses syllabus) {
        specializationSyllabusesDAO.addSyllabus(syllabus);
    }

    @Override
    public SpecializationSyllabuses getSyllabusById(String syllabusId) {
        return specializationSyllabusesDAO.getSyllabusById(syllabusId);
    }

    @Override
    public List<SpecializationSyllabuses> getSyllabusesBySubject(SpecializedSubject subject) {
        return specializationSyllabusesDAO.getSyllabusesBySubject(subject);
    }

    @Override
    public void deleteSyllabusBySubject(SpecializedSubject subject) {
        specializationSyllabusesDAO.deleteSyllabusBySubject(subject);
    }

    @Override
    public List<SpecializationSyllabuses> getPaginatedSyllabuses(String subjectId, int firstResult, int pageSize) {
        return specializationSyllabusesDAO.getPaginatedSyllabuses(subjectId, firstResult, pageSize);
    }

    @Override
    public Long numberOfSyllabuses(String subjectId) {
        return specializationSyllabusesDAO.numberOfSyllabuses(subjectId);
    }
}
