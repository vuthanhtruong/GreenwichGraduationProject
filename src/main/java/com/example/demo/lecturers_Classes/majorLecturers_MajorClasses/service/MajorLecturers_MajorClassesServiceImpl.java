package com.example.demo.lecturers_Classes.majorLecturers_MajorClasses.service;

import com.example.demo.classes.majorClasses.model.MajorClasses;
import com.example.demo.lecturers_Classes.majorLecturers_MajorClasses.dao.MajorLecturers_MajorClassesDAO;
import com.example.demo.lecturers_Classes.majorLecturers_MajorClasses.model.MajorLecturers_MajorClasses;
import com.example.demo.user.majorLecturer.model.MajorLecturers;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class MajorLecturers_MajorClassesServiceImpl implements MajorLecturers_MajorClassesService {
    @Override
    public List<String> getClassNotificationsForLecturer(String lecturerId) {
        return lecturers_classesDAO.getClassNotificationsForLecturer(lecturerId);
    }

    @Override
    public List<MajorLecturers_MajorClasses> getClassByLecturer(MajorLecturers lecturers) {
        return lecturers_classesDAO.getClassByLecturer(lecturers);
    }

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

    private final MajorLecturers_MajorClassesDAO lecturers_classesDAO;

    public MajorLecturers_MajorClassesServiceImpl(MajorLecturers_MajorClassesDAO lecturers_classesDAO) {
        this.lecturers_classesDAO = lecturers_classesDAO;
    }


}
