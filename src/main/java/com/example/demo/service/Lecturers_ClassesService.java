package com.example.demo.service;

import com.example.demo.entity.MajorClasses;
import com.example.demo.entity.MajorLecturers;
import com.example.demo.entity.Lecturers_MajorClasses;

import java.util.List;

public interface Lecturers_ClassesService {
    List<Lecturers_MajorClasses> listLecturersInClass(MajorClasses classes);
    List<MajorLecturers> listLecturersNotInClass(MajorClasses classes);
    void addLecturersToClass(MajorClasses classes, List<String> lecturerIds);
    void removeLecturerFromClass(MajorClasses classes, List<String> lecturerIds);
}
