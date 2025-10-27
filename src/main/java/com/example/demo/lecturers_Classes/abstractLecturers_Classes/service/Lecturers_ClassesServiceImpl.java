package com.example.demo.lecturers_Classes.abstractLecturers_Classes.service;

import com.example.demo.lecturers_Classes.abstractLecturers_Classes.dao.Lecturers_ClassesDAO;
import com.example.demo.lecturers_Classes.abstractLecturers_Classes.model.Lecturers_Classes;
import com.example.demo.user.majorLecturer.model.MajorLecturers;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Lecturers_ClassesServiceImpl implements Lecturers_ClassesService {
    @Override
    public List<Lecturers_Classes> getClassesByLecturer(MajorLecturers lecturer) {
        return lecturers_ClassesDAO.getClassesByLecturer(lecturer);
    }

    @Override
    public List<Lecturers_Classes> getClassesByLecturer(MajorLecturers lecturer, int firstResult, int pageSize) {
        return lecturers_ClassesDAO.getClassesByLecturer(lecturer, firstResult, pageSize);
    }

    @Override
    public long countClassesByLecturer(MajorLecturers lecturer) {
        return lecturers_ClassesDAO.countClassesByLecturer(lecturer);
    }

    private final Lecturers_ClassesDAO lecturers_ClassesDAO;

    public Lecturers_ClassesServiceImpl(Lecturers_ClassesDAO lecturersClassesDAO) {
        lecturers_ClassesDAO = lecturersClassesDAO;
    }

    @Override
    public List<Lecturers_Classes> getLecturers_ClassesByClassId(String classId) {
        return lecturers_ClassesDAO.getLecturers_ClassesByClassId(classId);
    }
}
