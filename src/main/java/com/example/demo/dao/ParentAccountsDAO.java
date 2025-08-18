package com.example.demo.dao;

import com.example.demo.entity.ParentAccounts;
import com.example.demo.entity.Student_ParentAccounts;
import com.example.demo.entity.Students;
import com.example.demo.entity.Enums.RelationshipToStudent;

import java.util.List;

public interface ParentAccountsDAO {
    void addParentAccounts(ParentAccounts parent);
    void updateParent(ParentAccounts parent);
    ParentAccounts findByEmail(String email);
    Student_ParentAccounts getParentLinkByStudentId(String studentId);
    void removeParentLink(Student_ParentAccounts parentLink);
    Student_ParentAccounts linkStudentToParent(Students student, ParentAccounts parent, RelationshipToStudent relationshipToStudent);
    long countLinkedStudents(String parentId, String excludeStudentId);
    List<String> validateParent(ParentAccounts parent);
    List<String> ParentValidation(ParentAccounts parent);
    String generateUniqueParentId();
    String generateRandomPassword(int length);
}