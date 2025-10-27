package com.example.demo.classes.minorClasses.service;

import com.example.demo.classes.minorClasses.dao.MinorClassesDAO;
import com.example.demo.classes.minorClasses.model.MinorClasses;
import com.example.demo.subject.minorSubject.model.MinorSubjects;
import com.example.demo.user.deputyStaff.model.DeputyStaffs;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MinorClassesServiceImpl implements MinorClassesService {
    private final MinorClassesDAO classesDAO;

    public MinorClassesServiceImpl(MinorClassesDAO classesDAO) {
        this.classesDAO = classesDAO;
    }

    @Override
    public List<MinorClasses> getClasses() {
        return classesDAO.getClasses();
    }

    @Override
    public MinorClasses getClassById(String id) {
        return classesDAO.getClassById(id);
    }

    @Override
    public MinorClasses getClassByName(String name) {
        return classesDAO.getClassByName(name);
    }

    @Override
    public void addClass(MinorClasses c) {
        classesDAO.addClass(c);
    }

    @Override
    public MinorClasses editClass(String id, MinorClasses c) {
        return classesDAO.editClass(id, c);
    }

    @Override
    public void deleteClass(String id) {
        classesDAO.deleteClass(id);
    }

    @Override
    public String generateUniqueClassId(LocalDateTime createdDate) {
        return classesDAO.generateUniqueClassId(createdDate);
    }

    @Override
    public List<String> validateClass(MinorClasses classObj, String excludeId) {
        return classesDAO.validateClass(classObj, excludeId);
    }

    @Override
    public List<MinorClasses> searchClasses(String searchType, String keyword, int firstResult, int pageSize ) {
        return classesDAO.searchClasses(searchType, keyword, firstResult, pageSize);
    }

    @Override
    public long countSearchResults(String searchType, String keyword ) {
        return classesDAO.countSearchResults(searchType, keyword);
    }

    @Override
    public List<MinorClasses> getPaginatedClasses(int firstResult, int pageSize ) {
        return classesDAO.getPaginatedClasses(firstResult, pageSize);
    }

    @Override
    public long numberOfClasses() {
        return classesDAO.numberOfClasses();
    }

    @Override
    public void setNullWhenDeletingSubject(MinorSubjects subject) {
        classesDAO.setNullWhenDeletingSubject(subject);
    }

    @Override
    public void deleteClassBySubject(MinorSubjects subject) {
        classesDAO.deleteClassBySubject(subject);
    }
}