package com.example.demo.lecturer_class.service;

import com.example.demo.lecturer.model.MajorLecturers;
import com.example.demo.specializedClasses.model.SpecializedClasses;

import java.util.List;

public interface MajorLecturers_SpecializedClassesService {
    List<MajorLecturers> listLecturersInClass(SpecializedClasses classes);
    List<MajorLecturers> listLecturersNotInClass(SpecializedClasses classes);
    void addLecturersToClass(SpecializedClasses classes, List<String> lecturerIds);
    void removeLecturerFromClass(SpecializedClasses classes, List<String> lecturerIds);
}
