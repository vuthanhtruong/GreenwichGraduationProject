package com.example.demo.service.impl;

import com.example.demo.dao.ClassesDAO;
import com.example.demo.entity.MajorClasses;
import com.example.demo.entity.Majors;
import com.example.demo.entity.MajorSubjects;
import com.example.demo.service.ClassesService;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ClassesServiceImpl implements ClassesService {


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
    public MajorClasses updateClass(String id, MajorClasses c) {
        return classesDAO.updateClass(id, c);
    }

    @Override
    public void deleteClass(String id) {
        classesDAO.deleteClass(id);
    }
}
