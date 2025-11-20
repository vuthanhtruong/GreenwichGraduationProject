package com.example.demo.user.parentAccount.service;

import com.example.demo.user.parentAccount.dao.ParentAccountsDAO;
import com.example.demo.user.parentAccount.model.ParentAccounts;
import com.example.demo.user.parentAccount.model.Student_ParentAccounts;
import com.example.demo.entity.Enums.RelationshipToStudent;
import com.example.demo.user.student.model.Students;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ParentAccountsServiceImpl implements ParentAccountsService {
    @Override
    public void editParent(ParentAccounts parent, MultipartFile avatarFile) throws IOException {
        parentAccountsDAO.editParent(parent, avatarFile);
    }

    @Override
    public Map<String, String> validateParent(ParentAccounts parent, MultipartFile avatarFile) {
        return parentAccountsDAO.validateParent(parent, avatarFile);
    }

    @Override
    public boolean isParentEmailAvailable(String email) {
        return parentAccountsDAO.isParentEmailAvailable(email);
    }

    @Override
    public List<Students> getStudentsByParentId(String parentId) {
        return parentAccountsDAO.getStudentsByParentId(parentId);
    }

    @Override
    public ParentAccounts getParent() {
        return parentAccountsDAO.getParent();
    }

    @Override
    public Student_ParentAccounts findLinkByStudentAndParent(String studentId, String parentId) {
        return parentAccountsDAO.findLinkByStudentAndParent(studentId, parentId);
    }

    @Override
    public void removeParentLinkByIds(String studentId, String parentId) {
        parentAccountsDAO.removeParentLinkByIds(studentId, parentId);
    }

    @Override
    public Map<String, String> validateParentLink(String email, String supportPhoneNumber, String relationship, String parentLabel) {
        return parentAccountsDAO.validateParentLink(email, supportPhoneNumber, relationship, parentLabel);
    }

    @Override
    public Map<String, String> validateParent(ParentAccounts parent) {
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
    public void editParent(ParentAccounts parent) {
        parentAccountsDAO.editParent(parent);
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
    public void editParentLink(Student_ParentAccounts existingLink, RelationshipToStudent relationship, String supportPhoneNumber) {
        parentAccountsDAO.editParentLink(existingLink, relationship, supportPhoneNumber);
    }

    @Override
    public long countLinkedStudents(String parentId, String excludeStudentId) {
        return parentAccountsDAO.countLinkedStudents(parentId, excludeStudentId);
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
    public void editOrCreateParentLink(String studentId, Student_ParentAccounts existingLink, String email, String supportPhoneNumber, String relationship) {
        parentAccountsDAO.editOrCreateParentLink(studentId, existingLink, email, supportPhoneNumber, relationship);
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