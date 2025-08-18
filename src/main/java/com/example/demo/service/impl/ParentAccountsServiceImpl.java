package com.example.demo.service.impl;

import com.example.demo.dao.ParentAccountsDAO;
import com.example.demo.entity.ParentAccounts;
import com.example.demo.entity.Student_ParentAccounts;
import com.example.demo.entity.Students;
import com.example.demo.entity.Enums.RelationshipToStudent;
import com.example.demo.service.ParentAccountsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParentAccountsServiceImpl implements ParentAccountsService {
    @Override
    public List<Student_ParentAccounts> getParentLinksByStudentId(String studentId) {
        return parentAccountsDAO.getParentLinksByStudentId(studentId);
    }

    @Override
    public List<String> validateParent(ParentAccounts parent) {
        return parentAccountsDAO.validateParent(parent);
    }

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
    public ParentAccounts findByEmail(String email) {
        return parentAccountsDAO.findByEmail(email);
    }

    @Override
    public Student_ParentAccounts getParentLinkByStudentId(String studentId) {
        return parentAccountsDAO.getParentLinkByStudentId(studentId);
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
    public long countLinkedStudents(String parentId, String excludeStudentId) {
        return parentAccountsDAO.countLinkedStudents(parentId, excludeStudentId);
    }

    @Override
    public List<String> ParentValidation(ParentAccounts parent) {
        return parentAccountsDAO.ParentValidation(parent);
    }

    @Override
    public String generateUniqueParentId() {
        return parentAccountsDAO.generateUniqueParentId();
    }

    @Override
    public String generateRandomPassword(int length) {
        return parentAccountsDAO.generateRandomPassword(length);
    }
}