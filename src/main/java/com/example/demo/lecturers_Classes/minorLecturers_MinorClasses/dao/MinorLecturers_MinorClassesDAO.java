package com.example.demo.lecturers_Classes.minorLecturers_MinorClasses.dao;

import com.example.demo.classes.minorClasses.model.MinorClasses;
import com.example.demo.lecturers_Classes.minorLecturers_MinorClasses.model.MinorLecturers_MinorClasses;
import com.example.demo.user.minorLecturer.model.MinorLecturers;

import java.util.List;

public interface MinorLecturers_MinorClassesDAO {

    List<MinorLecturers_MinorClasses> getClassByLecturer(MinorLecturers lecturer);

    void addLecturersToClass(MinorClasses minorClass, List<String> lecturerIds);

    void removeLecturerFromClass(MinorClasses minorClass, List<String> lecturerIds);

    List<MinorLecturers> listLecturersInClass(MinorClasses minorClass);

    List<MinorLecturers> listLecturersNotInClass(MinorClasses minorClass);

    List<String> getClassNotificationsForLecturer(String lecturerId);

    // Trong MinorLecturers_MinorClassesDAO.java (interface)
    long countLecturersTeachingMinorClasses();                    // Tổng GV đang dạy ít nhất 1 lớp minor
    long countMinorClassesWithoutLecturer();                      // Số lớp minor chưa có GV (CẢNH BÁO ĐỎ)
    List<Object[]> getTop5LecturersByMinorClassCount();           // Top 5 GV dạy nhiều lớp minor nhất
    List<Object[]> getTop5MinorClassesWithMostLecturers();        // Top 5 lớp minor có nhiều GV nhất
    List<Object[]> getTop5LecturersWithFewestMinorClasses();      // Top 5 GV dạy ít lớp nhất → gợi ý phân công
}