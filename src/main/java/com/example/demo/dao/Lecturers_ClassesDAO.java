package com.example.demo.dao;

import com.example.demo.entity.Classes;
import com.example.demo.entity.Lecturers_Classes;
import com.example.demo.entity.Lecturers;

import java.util.List;

public interface Lecturers_ClassesDAO {
    List<Lecturers_Classes> listLecturersInClass(Classes classes);
    List<Lecturers> listLecturersNotInClass(Classes classes);
    void addLecturersToClass(Classes classes, List<String> lecturerIds);
    void removeLecturerFromClass(Classes classes, List<String> lecturerIds);
}