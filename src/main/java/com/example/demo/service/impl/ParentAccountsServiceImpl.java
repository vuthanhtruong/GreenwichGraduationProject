package com.example.demo.service.impl;

import com.example.demo.dao.ParentAccountsDAO;
import com.example.demo.entity.Authenticators;
import com.example.demo.entity.ParentAccounts;
import com.example.demo.entity.Student_ParentAccounts;
import com.example.demo.entity.Students;

import com.example.demo.service.ParentAccountsService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParentAccountsServiceImpl implements ParentAccountsService {
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

    private final ParentAccountsDAO  parentAccountsDAO;

    public ParentAccountsServiceImpl(ParentAccountsDAO parentAccountsDAO) {
        this.parentAccountsDAO = parentAccountsDAO;
    }

    @Override
    public void addParentAccounts(ParentAccounts parent) {
        parentAccountsDAO.addParentAccounts(parent);
    }

    @Override
    public ParentAccounts findByEmail(String email) {
        return parentAccountsDAO.findByEmail(email);
    }

    @Override
    public Student_ParentAccounts linkStudentToParent(Students student, ParentAccounts parent) {
        return parentAccountsDAO.linkStudentToParent(student, parent);
    }

    @Override
    public List<String> validateParent(ParentAccounts parent) {
        return parentAccountsDAO.validateParent(parent);
    }
}
