package com.example.demo.lecturers_Classes.minorLecturers_MinorClasses.service;

import com.example.demo.classes.minorClasses.model.MinorClasses;
import com.example.demo.lecturers_Classes.minorLecturers_MinorClasses.dao.MinorLecturers_MinorClassesDAO;
import com.example.demo.lecturers_Classes.minorLecturers_MinorClasses.model.MinorLecturers_MinorClasses;
import com.example.demo.user.minorLecturer.model.MinorLecturers;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MinorLecturers_MinorClassesServiceImpl implements MinorLecturers_MinorClassesService {
    @Override
    public List<String> getClassNotificationsForLecturer(String lecturerId) {
        return dao.getClassNotificationsForLecturer(lecturerId);
    }

    private final MinorLecturers_MinorClassesDAO dao;

    public MinorLecturers_MinorClassesServiceImpl(MinorLecturers_MinorClassesDAO dao) {
        this.dao = dao;
    }

    @Override
    public List<MinorLecturers_MinorClasses> getClassByLecturer(MinorLecturers lecturer) {
        if (lecturer == null) {
            throw new IllegalArgumentException("Lecturer cannot be null");
        }
        return dao.getClassByLecturer(lecturer);
    }

    @Override
    public void addLecturersToClass(MinorClasses minorClass, List<String> lecturerIds) {
        if (minorClass == null) {
            throw new IllegalArgumentException("MinorClasses cannot be null");
        }
        if (lecturerIds == null || lecturerIds.isEmpty()) {
            throw new IllegalArgumentException("Lecturer IDs list cannot be null or empty");
        }
        dao.addLecturersToClass(minorClass, lecturerIds);
    }

    @Override
    public void removeLecturerFromClass(MinorClasses minorClass, List<String> lecturerIds) {
        if (minorClass == null) {
            throw new IllegalArgumentException("MinorClasses cannot be null");
        }
        if (lecturerIds == null || lecturerIds.isEmpty()) {
            throw new IllegalArgumentException("Lecturer IDs list cannot be null or empty");
        }
        dao.removeLecturerFromClass(minorClass, lecturerIds);
    }

    @Override
    public List<MinorLecturers> listLecturersInClass(MinorClasses minorClass) {
        if (minorClass == null) {
            throw new IllegalArgumentException("MinorClasses cannot be null");
        }
        return dao.listLecturersInClass(minorClass);
    }

    @Override
    public List<MinorLecturers> listLecturersNotInClass(MinorClasses minorClass) {
        if (minorClass == null) {
            throw new IllegalArgumentException("MinorClasses cannot be null");
        }
        return dao.listLecturersNotInClass(minorClass);
    }
}