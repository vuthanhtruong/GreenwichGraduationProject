package com.example.demo.classes.majorClasses.service;

import com.example.demo.classes.majorClasses.dao.MajorClassesDAO;
import com.example.demo.classes.majorClasses.model.MajorClasses;
import com.example.demo.major.model.Majors;
import com.example.demo.subject.majorSubject.model.MajorSubjects;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MajorClassesServiceImpl implements MajorClassesService {

    private final MajorClassesDAO classesDAO;

    public MajorClassesServiceImpl(MajorClassesDAO classesDAO) {
        this.classesDAO = classesDAO;
    }

    @Override
    public List<MajorClasses> getClassesByMajorAndCampus(Majors major, String campusId) {
        return classesDAO.getClassesByMajorAndCampus(major, campusId);
    }

    @Override
    public List<MajorClasses> searchClassesByCampus(String searchType, String keyword, int firstResult, int pageSize, Majors major, String campusId) {
        return classesDAO.searchClassesByCampus(searchType, keyword, firstResult, pageSize, major, campusId);
    }

    @Override
    public long countSearchResultsByCampus(String searchType, String keyword, Majors major, String campusId) {
        return classesDAO.countSearchResultsByCampus(searchType, keyword, major, campusId);
    }

    @Override
    public List<MajorClasses> getPaginatedClassesByCampus(int firstResult, int pageSize, Majors major, String campusId) {
        return classesDAO.getPaginatedClassesByCampus(firstResult, pageSize, major, campusId);
    }

    @Override
    public long numberOfClassesByCampus(Majors major, String campusId) {
        return classesDAO.numberOfClassesByCampus(major, campusId);
    }

    @Override
    public String generateUniqueClassId(String majorId, LocalDateTime createdDate) {
        return classesDAO.generateUniqueClassId(majorId, createdDate);
    }

    @Override
    public List<String> validateClass(MajorClasses classObj, String excludeId) {
        return classesDAO.validateClass(classObj, excludeId);
    }

    @Override
    public void deleteClassBySubject(MajorSubjects subject) {
        classesDAO.deleteClassBySubject(subject);
    }

    @Override
    public void SetNullWhenDeletingSubject(MajorSubjects subject) {
        classesDAO.SetNullWhenDeletingSubject(subject);
    }

    @Override
    public List<MajorClasses> getClasses() {
        return classesDAO.getClasses();
    }

    @Override
    public MajorClasses getClassById(String id) {
        return classesDAO.getClassById(id);
    }

    @Override
    public MajorClasses getClassByName(String name) {
        return classesDAO.getClassByName(name);
    }

    @Override
    public void addClass(MajorClasses c) {
        classesDAO.addClass(c);
    }

    @Override
    public MajorClasses editClass(String id, MajorClasses c) {
        return classesDAO.editClass(id, c);
    }

    @Override
    public void deleteClass(String id) {
        classesDAO.deleteClass(id);
    }
}