package com.example.demo.lecturers_Classes.minorLecturers_MinorClasses.service;

import com.example.demo.classes.minorClasses.model.MinorClasses;
import com.example.demo.lecturers_Classes.minorLecturers_MinorClasses.model.MinorLecturers_MinorClasses;
import com.example.demo.user.minorLecturer.model.MinorLecturers;

import java.util.List;

public interface MinorLecturers_MinorClassesService {
    List<MinorLecturers_MinorClasses> getClassByLecturer(MinorLecturers lecturer);

    void addLecturersToClass(MinorClasses minorClass, List<String> lecturerIds);

    void removeLecturerFromClass(MinorClasses minorClass, List<String> lecturerIds);

    List<MinorLecturers> listLecturersInClass(MinorClasses minorClass);

    List<MinorLecturers> listLecturersNotInClass(MinorClasses minorClass);
    List<String> getClassNotificationsForLecturer(String lecturerId);
}
