package com.example.demo.lecturers_Classes.majorLecturers_SpecializedClasses.service;

import com.example.demo.classes.specializedClasses.model.SpecializedClasses;
import com.example.demo.lecturers_Classes.majorLecturers_SpecializedClasses.model.MajorLecturers_SpecializedClasses;
import com.example.demo.user.majorLecturer.model.MajorLecturers;
import com.example.demo.lecturers_Classes.majorLecturers_SpecializedClasses.dao.MajorLecturers_SpecializedClassesDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MajorLecturers_SpecializedClassesServiceImpl implements MajorLecturers_SpecializedClassesService {
    @Override
    public List<MajorLecturers_SpecializedClasses> getClassByLecturer(MajorLecturers lecturers) {
        return majorLecturersSpecializedClassesDAO.getClassByLecturer(lecturers);
    }

    private final MajorLecturers_SpecializedClassesDAO majorLecturersSpecializedClassesDAO;

    @Autowired
    public MajorLecturers_SpecializedClassesServiceImpl(MajorLecturers_SpecializedClassesDAO majorLecturersSpecializedClassesDAO) {
        this.majorLecturersSpecializedClassesDAO = majorLecturersSpecializedClassesDAO;
    }

    @Override
    public void addLecturersToClass(SpecializedClasses classes, List<String> lecturerIds) {
        if (classes == null || lecturerIds == null || lecturerIds.isEmpty()) {
            throw new IllegalArgumentException("Class or lecturer IDs cannot be null or empty");
        }
        majorLecturersSpecializedClassesDAO.addLecturersToClass(classes, lecturerIds);
    }

    @Override
    public void removeLecturerFromClass(SpecializedClasses classes, List<String> lecturerIds) {
        if (classes == null || lecturerIds == null || lecturerIds.isEmpty()) {
            throw new IllegalArgumentException("Class or lecturer IDs cannot be null or empty");
        }
        majorLecturersSpecializedClassesDAO.removeLecturerFromClass(classes, lecturerIds);
    }

    @Override
    public List<MajorLecturers> listLecturersInClass(SpecializedClasses classes) {
        if (classes == null) {
            throw new IllegalArgumentException("Class cannot be null");
        }
        return majorLecturersSpecializedClassesDAO.listLecturersInClass(classes);
    }

    @Override
    public List<MajorLecturers> listLecturersNotInClass(SpecializedClasses classes) {
        if (classes == null) {
            throw new IllegalArgumentException("Class cannot be null");
        }
        return majorLecturersSpecializedClassesDAO.listLecturersNotInClass(classes);
    }
}