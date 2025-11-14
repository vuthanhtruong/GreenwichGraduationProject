package com.example.demo.classes.minorClasses.service;

import com.example.demo.classes.minorClasses.dao.MinorClassesDAO;
import com.example.demo.classes.minorClasses.model.MinorClasses;
import com.example.demo.subject.minorSubject.model.MinorSubjects;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class MinorClassesServiceImpl implements MinorClassesService {
    @Override
    public Map<String, String> validateClass(MinorClasses classObj, String excludeId) {
        return classesDAO.validateClass(classObj, excludeId);
    }

    private final MinorClassesDAO classesDAO;

    public MinorClassesServiceImpl(MinorClassesDAO classesDAO) {
        this.classesDAO = classesDAO;
    }

    @Override public List<MinorClasses> getClasses() { return classesDAO.getClasses(); }
    @Override public MinorClasses getClassById(String id) { return classesDAO.getClassById(id); }
    @Override public MinorClasses getClassByName(String name) { return classesDAO.getClassByName(name); }
    @Override public void addClass(MinorClasses c) { classesDAO.addClass(c); }
    @Override public MinorClasses editClass(String id, MinorClasses c) { return classesDAO.editClass(id, c); }
    @Override public void deleteClass(String id) { classesDAO.deleteClass(id); }
    @Override public String generateUniqueClassId(LocalDateTime createdDate) { return classesDAO.generateUniqueClassId(createdDate); }

    @Override
    public List<MinorClasses> searchClassesByCampus(String searchType, String keyword, int firstResult, int pageSize, String campusId) {
        return classesDAO.searchClassesByCampus(searchType, keyword, firstResult, pageSize, campusId);
    }

    @Override
    public long countSearchResultsByCampus(String searchType, String keyword, String campusId) {
        return classesDAO.countSearchResultsByCampus(searchType, keyword, campusId);
    }

    @Override
    public List<MinorClasses> getPaginatedClassesByCampus(int firstResult, int pageSize, String campusId) {
        return classesDAO.getPaginatedClassesByCampus(firstResult, pageSize, campusId);
    }

    @Override
    public long numberOfClassesByCampus(String campusId) {
        return classesDAO.numberOfClassesByCampus(campusId);
    }

    @Override public void setNullWhenDeletingSubject(MinorSubjects subject) { classesDAO.setNullWhenDeletingSubject(subject); }
    @Override public void deleteClassBySubject(MinorSubjects subject) { classesDAO.deleteClassBySubject(subject); }
}