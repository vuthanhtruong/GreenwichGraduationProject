package com.example.demo.parentAccount.service;

import com.example.demo.parentAccount.dao.ParentAccountsDAO;
import com.example.demo.parentAccount.model.ParentAccounts;
import com.example.demo.entity.Student_ParentAccounts;
import com.example.demo.entity.Enums.RelationshipToStudent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ParentAccountsServiceImpl implements ParentAccountsService {

    private final ParentAccountsDAO parentAccountsDAO;

    @Autowired
    public ParentAccountsServiceImpl(ParentAccountsDAO parentAccountsDAO) {
        this.parentAccountsDAO = parentAccountsDAO;
    }

    @Override
    public void addParentAccounts(ParentAccounts parent) {
        parentAccountsDAO.addParentAccounts(parent);
    }

    @Override
    public void updateParent(ParentAccounts parent) {
        parentAccountsDAO.updateParent(parent);
    }

    @Override
    public void deleteParent(ParentAccounts parent) {
        parentAccountsDAO.deleteParent(parent);
    }

    @Override
    public ParentAccounts findByEmail(String email) {
        return parentAccountsDAO.findByEmail(email);
    }

    @Override
    public Student_ParentAccounts getParentLinkByStudentId(String studentId) {
        return parentAccountsDAO.getParentLinkByStudentId(studentId);
    }

    @Override
    public List<Student_ParentAccounts> getParentLinksByStudentId(String studentId) {
        return parentAccountsDAO.getParentLinksByStudentId(studentId);
    }

    @Override
    public void removeParentLink(Student_ParentAccounts parentLink) {
        parentAccountsDAO.removeParentLink(parentLink);
    }

    @Override
    public Student_ParentAccounts linkStudentToParent(Student_ParentAccounts studentParent) {
        return parentAccountsDAO.linkStudentToParent(studentParent);
    }

    @Override
    public void updateParentLink(Student_ParentAccounts existingLink, RelationshipToStudent relationship, String supportPhoneNumber) {
        parentAccountsDAO.updateParentLink(existingLink, relationship, supportPhoneNumber);
    }

    @Override
    public long countLinkedStudents(String parentId, String excludeStudentId) {
        return parentAccountsDAO.countLinkedStudents(parentId, excludeStudentId);
    }

    @Override
    public List<String> validateParent(ParentAccounts parent) {
        return parentAccountsDAO.validateParent(parent);
    }

    @Override
    public List<String> validateParentLink(String email, String supportPhoneNumber, String relationship, String parentLabel) {
        return parentAccountsDAO.validateParentLink(email, supportPhoneNumber, relationship, parentLabel);
    }

    @Override
    public String generateUniqueParentId() {
        return parentAccountsDAO.generateUniqueParentId();
    }

    @Override
    public String generateRandomPassword(int length) {
        return parentAccountsDAO.generateRandomPassword(length);
    }

    @Override
    public void updateOrCreateParentLink(String studentId, Student_ParentAccounts existingLink, String email, String supportPhoneNumber, String relationship) {
        parentAccountsDAO.updateOrCreateParentLink(studentId, existingLink, email, supportPhoneNumber, relationship);
    }

    @Override
    public void createParentLink(String studentId, String email, String supportPhoneNumber, String relationship) {
        parentAccountsDAO.createParentLink(studentId, email, supportPhoneNumber, relationship);
    }

    @Override
    public void deleteIfUnlinked(ParentAccounts parent, String excludeStudentId) {
        parentAccountsDAO.deleteIfUnlinked(parent, excludeStudentId);
    }
}