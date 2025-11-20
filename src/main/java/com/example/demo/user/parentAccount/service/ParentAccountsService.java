package com.example.demo.user.parentAccount.service;

import com.example.demo.entity.Enums.RelationshipToStudent;
import com.example.demo.user.parentAccount.model.ParentAccounts;
import com.example.demo.user.parentAccount.model.Student_ParentAccounts;

import java.util.List;
import java.util.Map;

public interface ParentAccountsService {
    void addParentAccounts(ParentAccounts parent);
    void editParent(ParentAccounts parent);
    void deleteParent(ParentAccounts parent);
    ParentAccounts findByEmail(String email);
    Student_ParentAccounts getParentLinkByStudentId(String studentId);
    List<Student_ParentAccounts> getParentLinksByStudentId(String studentId);
    void removeParentLink(Student_ParentAccounts parentLink);
    Student_ParentAccounts linkStudentToParent(Student_ParentAccounts studentParent);
    long countLinkedStudents(String parentId, String excludeStudentId);
    Map<String, String> validateParent(ParentAccounts parent);
    Map<String, String> validateParentLink(String email, String supportPhoneNumber, String relationship, String parentLabel);
    String generateUniqueParentId();
    String generateRandomPassword(int length);
    void editParentLink(Student_ParentAccounts existingLink, RelationshipToStudent relationship, String supportPhoneNumber);
    void editOrCreateParentLink(String studentId, Student_ParentAccounts existingLink, String email, String supportPhoneNumber, String relationship);
    void createParentLink(String studentId, String email, String supportPhoneNumber, String relationship);
    void deleteIfUnlinked(ParentAccounts parent, String excludeStudentId);
    Student_ParentAccounts findLinkByStudentAndParent(String studentId, String parentId);
    void removeParentLinkByIds(String studentId, String parentId);
}
