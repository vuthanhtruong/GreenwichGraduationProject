package com.example.demo.lecturer_class.service;

import com.example.demo.classes.model.MajorClasses;
import com.example.demo.lecturer.model.MajorLecturers;
import com.example.demo.lecturer_class.model.MajorLecturers_MajorClasses;

import java.util.List;

public interface Lecturers_ClassesService {
    List<MajorLecturers> listLecturersInClass(MajorClasses classes);
    List<MajorLecturers> listLecturersNotInClass(MajorClasses classes);
    void addLecturersToClass(MajorClasses classes, List<String> lecturerIds);
    void removeLecturerFromClass(MajorClasses classes, List<String> lecturerIds);
}
