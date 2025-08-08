package com.example.demo.service;

import com.example.demo.entity.Classes;
import com.example.demo.entity.Lecturers;
import com.example.demo.entity.Lecturers_Classes;

import java.util.List;

public interface Lecturers_ClassesService {
    List<Lecturers_Classes> listLecturersInClass(Classes classes);
    List<Lecturers> listLecturersNotInClass(Classes classes);
    void addLecturersToClass(Classes classes, List<String> lecturerIds);
    void removeLecturerFromClass(Classes classes, List<String> lecturerIds);
}
