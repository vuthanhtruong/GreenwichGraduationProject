package com.example.demo.service.impl;

import com.example.demo.dao.ClassesDAO;
import com.example.demo.entity.Classes;
import com.example.demo.entity.Majors;
import com.example.demo.entity.Subjects;
import com.example.demo.service.ClassesService;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ClassesServiceImpl implements ClassesService {


    @Override
    public void deleteClassBySubject(Subjects subject) {
        classesDAO.deleteClassBySubject(subject);
    }

    @Override
    public void SetNullWhenDeletingSubject(Subjects subject) {
        classesDAO.SetNullWhenDeletingSubject(subject);
    }

    @Override
    public List<Classes> ClassesByMajor(Majors major) {
        return classesDAO.ClassesByMajor(major);
    }

    private final ClassesDAO classesDAO;

    public ClassesServiceImpl(ClassesDAO classesDAO) {
        this.classesDAO = classesDAO;
    }

    @Override
    public List<Classes> getClasses() {
        return classesDAO.getClasses();
    }

    @Override
    public Classes getClassById(String id) {
        return classesDAO.getClassById(id);
    }

    @Override
    public Classes getClassByName(String name) {
        return classesDAO.getClassByName(name);
    }

    @Override
    public void addClass(Classes c) {
        classesDAO.addClass(c);
    }

    @Override
    public Classes updateClass(String id,Classes c) {
        return classesDAO.updateClass(id, c);
    }

    @Override
    public void deleteClass(String id) {
        classesDAO.deleteClass(id);
    }
}
