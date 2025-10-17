package com.example.demo.lecturers_Classes.majorLecturers_MajorClasses.service;

import com.example.demo.classes.majorClasses.model.MajorClasses;
import com.example.demo.lecturers_Classes.majorLecturers_MajorClasses.model.MajorLecturers_MajorClasses;
import com.example.demo.user.majorLecturer.model.MajorLecturers;

import java.util.List;

public interface MajorLecturers_MajorClassesService {
    List<MajorLecturers> listLecturersInClass(MajorClasses classes);
    List<MajorLecturers> listLecturersNotInClass(MajorClasses classes);
    void addLecturersToClass(MajorClasses classes, List<String> lecturerIds);
    void removeLecturerFromClass(MajorClasses classes, List<String> lecturerIds);
    List<MajorLecturers_MajorClasses> getClassByLecturer(MajorLecturers lecturers);
}
