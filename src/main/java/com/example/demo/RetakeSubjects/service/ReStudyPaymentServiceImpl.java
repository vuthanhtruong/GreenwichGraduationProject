// src/main/java/com/example/demo/restudy/service/ReStudyPaymentServiceImpl.java
package com.example.demo.RetakeSubjects.service;

import com.example.demo.RetakeSubjects.dao.ReStudyPaymentDAO;
import com.example.demo.RetakeSubjects.model.RetakeSubjects;
import com.example.demo.user.student.model.Students;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ReStudyPaymentServiceImpl implements ReStudyPaymentService {
    @Override
    public List<RetakeSubjects> getRetakeSubjectsBySubjectId(String subjectId) {
        return reStudyPaymentDAO.getRetakeSubjectsBySubjectId(subjectId);
    }

    @Override
    public Map<String, Object> validateBalance(Students student, List<String> selectedSubjectIds) {
        return reStudyPaymentDAO.validateBalance(student, selectedSubjectIds);
    }

    @Override
    public void processReStudyPayment(Students student, List<String> selectedSubjectIds) {
        reStudyPaymentDAO.processReStudyPayment(student, selectedSubjectIds);
    }

    private final ReStudyPaymentDAO reStudyPaymentDAO;

    public ReStudyPaymentServiceImpl(ReStudyPaymentDAO reStudyPaymentDAO) {
        this.reStudyPaymentDAO = reStudyPaymentDAO;
    }

}