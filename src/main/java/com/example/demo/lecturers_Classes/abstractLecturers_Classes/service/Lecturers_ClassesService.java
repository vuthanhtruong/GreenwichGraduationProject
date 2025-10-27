package com.example.demo.lecturers_Classes.abstractLecturers_Classes.service;

import com.example.demo.lecturers_Classes.abstractLecturers_Classes.model.Lecturers_Classes;
import com.example.demo.user.majorLecturer.model.MajorLecturers;

import java.util.List;

public interface Lecturers_ClassesService {
    List<Lecturers_Classes> getLecturers_ClassesByClassId(String classId);
    List<Lecturers_Classes> getClassesByLecturer(MajorLecturers lecturer);
    List<Lecturers_Classes> getClassesByLecturer(MajorLecturers lecturer, int firstResult, int pageSize);
    long countClassesByLecturer(MajorLecturers lecturer);
}
