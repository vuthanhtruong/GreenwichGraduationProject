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
    @Override
    public List<MajorClasses> searchClasses(String searchType, String keyword, int firstResult, int pageSize, Majors major) {
        return classesDAO.searchClasses(searchType, keyword, firstResult, pageSize, major);
    }

    @Override
    public long countSearchResults(String searchType, String keyword, Majors major) {
        return classesDAO.countSearchResults(searchType, keyword, major);
    }

    @Override
    public List<MajorClasses> getPaginatedClasses(int firstResult, int pageSize, Majors major) {
        return classesDAO.getPaginatedClasses(firstResult, pageSize, major);
    }

    @Override
    public long numberOfClasses(Majors major) {
        return classesDAO.numberOfClasses(major);
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
    public List<MajorClasses> ClassesByMajor(Majors major) {
        return classesDAO.ClassesByMajor(major);
    }

    private final MajorClassesDAO classesDAO;

    public MajorClassesServiceImpl(MajorClassesDAO classesDAO) {
        this.classesDAO = classesDAO;
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
