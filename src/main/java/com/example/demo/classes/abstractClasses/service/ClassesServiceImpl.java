package com.example.demo.classes.abstractClasses.service;

import com.example.demo.classes.abstractClasses.dao.ClassesDAO;
import com.example.demo.classes.abstractClasses.model.Classes;
import org.springframework.stereotype.Service;

@Service
public class ClassesServiceImpl implements ClassesService {
    @Override
    public boolean existsByNameClass(String nameClass) {
        return classesDAO.existsByNameClass(nameClass);
    }

    @Override
    public boolean existsByNameClassExcludingId(String nameClass, String excludeClassId) {
        return classesDAO.existsByNameClassExcludingId(nameClass, excludeClassId);
    }

    private final ClassesDAO classesDAO;

    public ClassesServiceImpl(ClassesDAO classesDAO) {
        this.classesDAO = classesDAO;
    }

    @Override
    public Classes findClassById(String classId) {
        return classesDAO.findClassById(classId);
    }
}
