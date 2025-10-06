package com.example.demo.Curriculum.dao;

import com.example.demo.Curriculum.model.Curriculum;

import java.util.List;
import java.util.Map;

public interface CurriculumDAO {
    Curriculum getCurriculumById(String id);
    Curriculum getByName(String name);
    boolean existsByNameExcludingId(String name, String curriculumId);
    void addCurriculum(Curriculum curriculum);
    void updateCurriculum(Curriculum curriculum);
    void deleteCurriculum(String id);
    List<Curriculum> getCurriculums();
    Map<String, String> validateCurriculum(Curriculum curriculum);
    String generateUniqueCurriculumId();
}
