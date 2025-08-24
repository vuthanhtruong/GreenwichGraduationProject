package com.example.demo.classes.service;

import com.example.demo.classes.dao.ClassesDAO;
import com.example.demo.classes.model.MajorClasses;
import com.example.demo.major.model.Majors;
import com.example.demo.subject.model.MajorSubjects;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
@Service
public class ClassesServiceImpl implements ClassesService {
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

    private final ClassesDAO classesDAO;

    public ClassesServiceImpl(ClassesDAO classesDAO) {
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
