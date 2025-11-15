package com.example.demo.students_Classes.students_MinorClasses.service;

import com.example.demo.classes.minorClasses.model.MinorClasses;
import com.example.demo.students_Classes.students_MinorClasses.model.Students_MinorClasses;
import com.example.demo.user.student.model.Students;

import java.util.List;

public interface StudentsMinorClassesService {

    // Lấy danh sách sinh viên trong lớp Minor
    List<Students_MinorClasses> getStudentsInClass(String classId);

    // Lấy danh sách sinh viên theo MinorClasses entity
    List<Students> getStudentsByClass(MinorClasses minorClass);

    // Lấy danh sách sinh viên chưa trong lớp (cùng campus, chưa học môn này)
    List<Students> getStudentsNotInClassAndSubject(String classId, String subjectId);

    // Thêm sinh viên vào lớp
    void addStudentToClass(Students_MinorClasses studentsMinorClasses);

    // Xóa sinh viên khỏi lớp
    void removeStudentFromClass(String studentId, String classId);

    // Kiểm tra sinh viên đã trong lớp chưa
    boolean existsByStudentAndClass(String studentId, String classId);

    // Lấy danh sách lớp của sinh viên (dùng cho service tổng hợp)
    List<Students_MinorClasses> getStudentsInClassByStudent(String studentId);
    List<String> getClassNotificationsForStudent(String studentId);
}