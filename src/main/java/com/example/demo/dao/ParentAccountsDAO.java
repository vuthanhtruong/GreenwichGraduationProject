package com.example.demo.dao;

import com.example.demo.entity.ParentAccounts;
import com.example.demo.entity.Student_ParentAccounts;
import com.example.demo.entity.Students;
import com.example.demo.entity.Enums.RelationshipToStudent;

import java.util.List;

public interface ParentAccountsDAO {
    void addParentAccounts(ParentAccounts parent);
    void updateParent(ParentAccounts parent);
    void deleteParent(ParentAccounts parent);
    ParentAccounts findByEmail(String email);
    Student_ParentAccounts getParentLinkByStudentId(String studentId);
    List<Student_ParentAccounts> getParentLinksByStudentId(String studentId);
    void removeParentLink(Student_ParentAccounts parentLink);
    Student_ParentAccounts linkStudentToParent(Student_ParentAccounts studentParent);
    void updateParentLink(Student_ParentAccounts existingLink, RelationshipToStudent relationship, String supportPhoneNumber);
    long countLinkedStudents(String parentId, String excludeStudentId);
    List<String> validateParent(ParentAccounts parent);
    List<String> validateParentLink(String email, String supportPhoneNumber, String relationship, String parentLabel);
    String generateUniqueParentId();
    String generateRandomPassword(int length);
    void updateOrCreateParentLink(String studentId, Student_ParentAccounts existingLink, String email, String supportPhoneNumber, String relationship);
    void createParentLink(String studentId, String email, String supportPhoneNumber, String relationship);
    void deleteIfUnlinked(ParentAccounts parent, String excludeStudentId);
}