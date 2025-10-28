package com.example.demo.studentRequiredSubjects.studentRequiredMinorSubjects.dao;

import com.example.demo.subject.minorSubject.model.MinorSubjects;
import com.example.demo.studentRequiredSubjects.studentRequiredMinorSubjects.model.StudentRequiredMinorSubjects;
import com.example.demo.user.student.model.Students;

import java.util.List;

public interface StudentRequiredMinorSubjectsDAO {

    // Lấy danh sách sinh viên đã được phân môn Minor
    List<StudentRequiredMinorSubjects> getStudentRequiredMinorSubjects(MinorSubjects subject);

    // Lấy danh sách sinh viên chưa được phân môn Minor (cùng campus)
    List<Students> getStudentsNotRequiredMinorSubject(MinorSubjects subject);

    // Kiểm tra sinh viên đã được phân môn chưa
    boolean isStudentAlreadyRequiredForSubject(String studentId, String subjectId);

    // Thêm phân môn cho sinh viên
    void addStudentRequiredMinorSubject(StudentRequiredMinorSubjects srm);

    // Xóa phân môn
    boolean removeStudentRequiredMinorSubject(String studentId, String subjectId);
}