package com.example.demo.dao;

import com.example.demo.entity.MajorClasses;
import com.example.demo.entity.MajorLecturers_MajorClasses;
import com.example.demo.entity.MajorLecturers;

import java.util.List;

public interface Lecturers_ClassesDAO {
    List<MajorLecturers_MajorClasses> listLecturersInClass(MajorClasses classes);
    List<MajorLecturers> listLecturersNotInClass(MajorClasses classes);
    void addLecturersToClass(MajorClasses classes, List<String> lecturerIds);
    void removeLecturerFromClass(MajorClasses classes, List<String> lecturerIds);
}