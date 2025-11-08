// src/main/java/com/example/demo/RetakeSubjects/service/RetakeSubjectsService.java
package com.example.demo.retakeSubjects.service;

import com.example.demo.retakeSubjects.model.RetakeSubjects;
import com.example.demo.user.student.model.Students;
import java.util.List;
import java.util.Map;

public interface RetakeSubjectsService {
    void save(RetakeSubjects retakeSubjects);
    boolean existsByStudentAndSubject(String studentId, String subjectId);
    List<RetakeSubjects> getRetakeSubjectsBySubjectId(String subjectId);

    Map<String, Object> validateBalance(Students student, List<String> selectedSubjectIds);
    void processReStudyPayment(Students student, List<String> selectedSubjectIds);

    List<Students> getStudentsWithSufficientBalance(String subjectId, List<Students> candidates);
    List<Students> getStudentsWithInsufficientBalance(String subjectId, List<Students> candidates);
    boolean deductAndLogPayment(Students student, String subjectId, Double amount);
}