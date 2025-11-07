package com.example.demo.RetakeSubjects.dao;

import com.example.demo.RetakeSubjects.model.RetakeSubjects;
import com.example.demo.user.student.model.Students;
import java.util.List;
import java.util.Map;

public interface ReStudyPaymentDAO {

    // Trả Map: chỉ kiểm tra số dư
    Map<String, Object> validateBalance(Students student, List<String> selectedSubjectIds);
    void processReStudyPayment(Students student, List<String> selectedSubjectIds);
    List<RetakeSubjects> getRetakeSubjectsBySubjectId(String subjectId);
}