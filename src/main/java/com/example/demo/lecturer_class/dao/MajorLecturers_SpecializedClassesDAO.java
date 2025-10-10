package com.example.demo.lecturer_class.dao;
import com.example.demo.lecturer.model.MajorLecturers;
import com.example.demo.specializedClasses.model.SpecializedClasses;

import java.util.List;

public interface MajorLecturers_SpecializedClassesDAO {
    List<MajorLecturers> listLecturersInClass(SpecializedClasses classes);
    List<MajorLecturers> listLecturersNotInClass(SpecializedClasses classes);
    void addLecturersToClass(SpecializedClasses classes, List<String> lecturerIds);
    void removeLecturerFromClass(SpecializedClasses classes, List<String> lecturerIds);
}
