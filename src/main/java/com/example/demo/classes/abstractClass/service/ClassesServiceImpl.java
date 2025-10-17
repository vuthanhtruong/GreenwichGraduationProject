package com.example.demo.classes.abstractClass.service;

import com.example.demo.classes.abstractClass.dao.ClassesDAO;
import com.example.demo.classes.abstractClass.model.Classes;
import org.springframework.stereotype.Service;

@Service
public class ClassesServiceImpl implements ClassesService {
    private final ClassesDAO classesDAO;

    public ClassesServiceImpl(ClassesDAO classesDAO) {
        this.classesDAO = classesDAO;
    }

    @Override
    public Classes findClassById(String classId) {
        return classesDAO.findClassById(classId);
    }
}
