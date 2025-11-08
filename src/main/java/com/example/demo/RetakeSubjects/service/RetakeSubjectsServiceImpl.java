// src/main/java/com/example/demo/RetakeSubjects/service/RetakeSubjectsServiceImpl.java
package com.example.demo.RetakeSubjects.service;

import com.example.demo.RetakeSubjects.dao.RetakeSubjectsDAO;
import com.example.demo.RetakeSubjects.model.RetakeSubjects;
import com.example.demo.user.student.model.Students;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class RetakeSubjectsServiceImpl implements RetakeSubjectsService {

    private final RetakeSubjectsDAO retakeSubjectsDAO;

    @Autowired
    public RetakeSubjectsServiceImpl(RetakeSubjectsDAO retakeSubjectsDAO) {
        this.retakeSubjectsDAO = retakeSubjectsDAO;
    }

    @Override public void save(RetakeSubjects retakeSubjects) { retakeSubjectsDAO.save(retakeSubjects); }
    @Override public boolean existsByStudentAndSubject(String studentId, String subjectId) { return retakeSubjectsDAO.existsByStudentAndSubject(studentId, subjectId); }
    @Override public List<RetakeSubjects> getRetakeSubjectsBySubjectId(String subjectId) { return retakeSubjectsDAO.getRetakeSubjectsBySubjectId(subjectId); }
    @Override public Map<String, Object> validateBalance(Students student, List<String> selectedSubjectIds) { return retakeSubjectsDAO.validateBalance(student, selectedSubjectIds); }
    @Override public void processReStudyPayment(Students student, List<String> selectedSubjectIds) { retakeSubjectsDAO.processReStudyPayment(student, selectedSubjectIds); }
    @Override public List<Students> getStudentsWithSufficientBalance(String subjectId, List<Students> candidates) { return retakeSubjectsDAO.getStudentsWithSufficientBalance(subjectId, candidates); }
    @Override public List<Students> getStudentsWithInsufficientBalance(String subjectId, List<Students> candidates) { return retakeSubjectsDAO.getStudentsWithInsufficientBalance(subjectId, candidates); }
    @Override public boolean deductAndLogPayment(Students student, String subjectId, Double amount) { return retakeSubjectsDAO.deductAndLogPayment(student, subjectId, amount); }
}