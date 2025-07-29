package com.example.demo.service.impl;

import com.example.demo.dao.Lecturers_ClassesDAO;
import com.example.demo.dao.LecturesDAO;
import com.example.demo.entity.Classes;
import com.example.demo.entity.Lecturers;
import com.example.demo.entity.Lecturers_Classes;
import com.example.demo.service.Lecturers_ClassesService;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class Lecturers_ClassesServiceImpl implements Lecturers_ClassesService {
    @Override
    public void addLecturersToClass(Classes classes, List<String> lecturerIds) {
        lecturers_classesDAO.addLecturersToClass(classes, lecturerIds);
    }

    @Override
    public List<Lecturers> listLecturersNotInClass(Classes classes) {
        return lecturers_classesDAO.listLecturersNotInClass(classes);
    }

    private Lecturers_ClassesDAO lecturers_classesDAO;

    public Lecturers_ClassesServiceImpl(Lecturers_ClassesDAO lecturers_classesDAO) {
        this.lecturers_classesDAO = lecturers_classesDAO;
    }

    @Override
    public List<Lecturers_Classes> listLecturersInClass(Classes classes) {
        return lecturers_classesDAO.listLecturersInClass(classes);
    }

}
