package com.example.demo.classes.specializedClasses.service;

import com.example.demo.classes.specializedClasses.dao.SpecializedClassesDAO;
import com.example.demo.classes.specializedClasses.model.SpecializedClasses;
import com.example.demo.major.model.Majors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class SpecializedClassesServiceImpl implements SpecializedClassesService {
    @Override
    public Map<String, String> validateClass(SpecializedClasses classObj, String excludeId) {
        return classesDAO.validateClass(classObj, excludeId);
    }

    private final SpecializedClassesDAO classesDAO;

    @Autowired
    public SpecializedClassesServiceImpl(SpecializedClassesDAO classesDAO) {
        this.classesDAO = classesDAO;
    }

    @Override
    public List<SpecializedClasses> getClassesByMajorAndCampus(Majors major, String campusId) {
        return classesDAO.getClassesByMajorAndCampus(major, campusId);
    }

    @Override
    public List<SpecializedClasses> searchClassesByCampus(String searchType, String keyword, int firstResult, int pageSize, Majors major, String campusId) {
        return classesDAO.searchClassesByCampus(searchType, keyword, firstResult, pageSize, major, campusId);
    }

    @Override
    public long countSearchResultsByCampus(String searchType, String keyword, Majors major, String campusId) {
        return classesDAO.countSearchResultsByCampus(searchType, keyword, major, campusId);
    }

    @Override
    public List<SpecializedClasses> getPaginatedClassesByCampus(int firstResult, int pageSize, Majors major, String campusId) {
        return classesDAO.getPaginatedClassesByCampus(firstResult, pageSize, major, campusId);
    }

    @Override
    public long numberOfClassesByCampus(Majors major, String campusId) {
        return classesDAO.numberOfClassesByCampus(major, campusId);
    }

    @Override
    public List<SpecializedClasses> getClasses() {
        return classesDAO.getClasses();
    }

    @Override
    public SpecializedClasses getClassById(String id) {
        return classesDAO.getClassById(id);
    }

    @Override
    public SpecializedClasses getClassByName(String name) {
        return classesDAO.getClassByName(name);
    }

    @Override
    public void addClass(SpecializedClasses c) {
        classesDAO.addClass(c);
    }

    @Override
    public SpecializedClasses editClass(String id, SpecializedClasses c) {
        return classesDAO.editClass(id, c);
    }

    @Override
    public void deleteClass(String id) {
        classesDAO.deleteClass(id);
    }

    @Override
    public String generateUniqueClassId(String specializationId, LocalDateTime createdDate) {
        return classesDAO.generateUniqueClassId(specializationId, createdDate);
    }

}