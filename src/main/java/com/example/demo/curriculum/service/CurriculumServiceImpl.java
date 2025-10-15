package com.example.demo.curriculum.service;

import com.example.demo.curriculum.dao.CurriculumDAO;
import com.example.demo.curriculum.model.Curriculum;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
@Service
public class CurriculumServiceImpl implements CurriculumService {
    @Override
    public String generateUniqueCurriculumId() {
        return curriculumDAO.generateUniqueCurriculumId();
    }

    private final CurriculumDAO curriculumDAO;

    public CurriculumServiceImpl(CurriculumDAO curriculumDAO) {
        this.curriculumDAO = curriculumDAO;
    }

    @Override
    public Curriculum getCurriculumById(String id) {
        return curriculumDAO.getCurriculumById(id);
    }

    @Override
    public Curriculum getByName(String name) {
        return curriculumDAO.getByName(name);
    }

    @Override
    public boolean existsByNameExcludingId(String name, String curriculumId) {
        return curriculumDAO.existsByNameExcludingId(name, curriculumId);
    }

    @Override
    public void addCurriculum(Curriculum curriculum) {
        curriculumDAO.addCurriculum(curriculum);
    }

    @Override
    public void updateCurriculum(Curriculum curriculum) {
        curriculumDAO.updateCurriculum(curriculum);
    }

    @Override
    public void deleteCurriculum(String id) {
        curriculumDAO.deleteCurriculum(id);
    }

    @Override
    public List<Curriculum> getCurriculums() {
        return curriculumDAO.getCurriculums();
    }

    @Override
    public Map<String, String> validateCurriculum(Curriculum curriculum) {
        return curriculumDAO.validateCurriculum(curriculum);
    }
}
