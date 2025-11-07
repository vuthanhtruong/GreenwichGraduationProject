// src/main/java/com/example/demo/restudy/service/ReStudyPaymentService.java
package com.example.demo.RetakeSubjects.service;

import com.example.demo.RetakeSubjects.model.RetakeSubjects;
import com.example.demo.user.student.model.Students;
import java.util.List;
import java.util.Map;

public interface ReStudyPaymentService {
    Map<String, Object> validateBalance(Students student, List<String> selectedSubjectIds);
    void processReStudyPayment(Students student, List<String> selectedSubjectIds);
    List<RetakeSubjects> getRetakeSubjectsBySubjectId(String subjectId);
}