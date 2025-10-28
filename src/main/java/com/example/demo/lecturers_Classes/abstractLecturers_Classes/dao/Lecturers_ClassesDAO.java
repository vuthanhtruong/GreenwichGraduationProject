package com.example.demo.lecturers_Classes.abstractLecturers_Classes.dao;

import com.example.demo.lecturers_Classes.abstractLecturers_Classes.model.Lecturers_Classes;
import com.example.demo.user.majorLecturer.model.MajorLecturers;

import java.util.List;

public interface Lecturers_ClassesDAO {
    List<Lecturers_Classes> getClassesByLecturer(MajorLecturers lecturer);
    List<Lecturers_Classes> getClassesByLecturer(MajorLecturers lecturer, int firstResult, int pageSize);
    long countClassesByLecturer(MajorLecturers lecturer);
}
