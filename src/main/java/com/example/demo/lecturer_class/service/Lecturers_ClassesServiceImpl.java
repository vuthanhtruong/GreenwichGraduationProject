package com.example.demo.lecturer_class.service;

import com.example.demo.lecturer_class.dao.Lecturers_ClassesDAO;
import com.example.demo.classes.model.MajorClasses;
import com.example.demo.lecturer.model.MajorLecturers;
import com.example.demo.lecturer_class.model.MajorLecturers_MajorClasses;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class Lecturers_ClassesServiceImpl implements Lecturers_ClassesService {
    @Override
    public List<MajorLecturers> listLecturersInClass(MajorClasses classes) {
        return lecturers_classesDAO.listLecturersInClass(classes);
    }

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

    private final Lecturers_ClassesDAO lecturers_classesDAO;

    public Lecturers_ClassesServiceImpl(Lecturers_ClassesDAO lecturers_classesDAO) {
        this.lecturers_classesDAO = lecturers_classesDAO;
    }


}
