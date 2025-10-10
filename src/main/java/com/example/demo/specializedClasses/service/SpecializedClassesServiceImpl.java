package com.example.demo.specializedClasses.service;

import com.example.demo.specializedClasses.dao.SpecializedClassesDAO;
import com.example.demo.specializedClasses.model.SpecializedClasses;
import com.example.demo.major.model.Majors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SpecializedClassesServiceImpl implements SpecializedClassesService {

    private final SpecializedClassesDAO classesDAO;

    @Autowired
    public SpecializedClassesServiceImpl(SpecializedClassesDAO classesDAO) {
        this.classesDAO = classesDAO;
    }

    @Override
    public List<SpecializedClasses> ClassesByMajor(Majors major) {
        return classesDAO.ClassesByMajor(major);
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
    public SpecializedClasses editClass(String id, SpecializedClasses classObj) {
        return classesDAO.editClass(id, classObj);
    }

    @Override
    public void deleteClass(String id) {
        classesDAO.deleteClass(id);
    }

    @Override
    public String generateUniqueClassId(String specializationId, LocalDateTime createdDate) {
        return classesDAO.generateUniqueClassId(specializationId, createdDate);
    }

    @Override
    public List<String> validateClass(SpecializedClasses classObj, String excludeId) {
        return classesDAO.validateClass(classObj, excludeId);
    }

    @Override
    public List<SpecializedClasses> searchClasses(String searchType, String keyword, int firstResult, int pageSize, Majors major) {
        return classesDAO.searchClasses(searchType, keyword, firstResult, pageSize, major);
    }

    @Override
    public long countSearchResults(String searchType, String keyword, Majors major) {
        return classesDAO.countSearchResults(searchType, keyword, major);
    }

    @Override
    public List<SpecializedClasses> getPaginatedClasses(int firstResult, int pageSize, Majors major) {
        return classesDAO.getPaginatedClasses(firstResult, pageSize, major);
    }

    @Override
    public long numberOfClasses(Majors major) {
        return classesDAO.numberOfClasses(major);
    }
}