package com.example.demo.service.impl;

import com.example.demo.dao.Lecturers_ClassesDAO;
import com.example.demo.entity.MajorClasses;
import com.example.demo.entity.MajorLecturers;
import com.example.demo.entity.Lecturers_MajorClasses;
import com.example.demo.service.Lecturers_ClassesService;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class Lecturers_ClassesServiceImpl implements Lecturers_ClassesService {
    @Override
    public void removeLecturerFromClass(MajorClasses classes, List<String> lecturerIds) {
        lecturers_classesDAO.removeLecturerFromClass(classes, lecturerIds);
    }

    @Override
    public void addLecturersToClass(MajorClasses classes, List<String> lecturerIds) {
        lecturers_classesDAO.addLecturersToClass(classes, lecturerIds);
    }

    @Override
    public List<MajorLecturers> listLecturersNotInClass(MajorClasses classes) {
        return lecturers_classesDAO.listLecturersNotInClass(classes);
    }

    private Lecturers_ClassesDAO lecturers_classesDAO;

    public Lecturers_ClassesServiceImpl(Lecturers_ClassesDAO lecturers_classesDAO) {
        this.lecturers_classesDAO = lecturers_classesDAO;
    }

    @Override
    public List<Lecturers_MajorClasses> listLecturersInClass(MajorClasses classes) {
        return lecturers_classesDAO.listLecturersInClass(classes);
    }

}
