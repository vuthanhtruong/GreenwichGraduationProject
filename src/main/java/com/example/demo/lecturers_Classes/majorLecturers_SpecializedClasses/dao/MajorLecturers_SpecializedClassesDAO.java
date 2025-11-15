package com.example.demo.lecturers_Classes.majorLecturers_SpecializedClasses.dao;
import com.example.demo.classes.specializedClasses.model.SpecializedClasses;
import com.example.demo.lecturers_Classes.majorLecturers_SpecializedClasses.model.MajorLecturers_SpecializedClasses;
import com.example.demo.user.majorLecturer.model.MajorLecturers;

import java.util.List;

public interface MajorLecturers_SpecializedClassesDAO {
    List<MajorLecturers> listLecturersInClass(SpecializedClasses classes);
    List<MajorLecturers> listLecturersNotInClass(SpecializedClasses classes);
    void addLecturersToClass(SpecializedClasses classes, List<String> lecturerIds);
    void removeLecturerFromClass(SpecializedClasses classes, List<String> lecturerIds);
    List<MajorLecturers_SpecializedClasses>  getClassByLecturer(MajorLecturers lecturers);
    List<String> getClassNotificationsForLecturer(String lecturerId);
}
