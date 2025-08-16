package com.example.demo.service;

import com.example.demo.entity.ParentAccounts;
import com.example.demo.entity.Student_ParentAccounts;
import com.example.demo.entity.Students;

import java.util.List;

public interface ParentAccountsService {
    void addParentAccounts(ParentAccounts parent);
    ParentAccounts findByEmail(String email);
    Student_ParentAccounts linkStudentToParent(Students student, ParentAccounts parent);
    List<String> validateParent(ParentAccounts parent);

    List<String> ParentValidation(ParentAccounts parent);
    String generateUniqueParentId();
    String generateRandomPassword(int length);
}
