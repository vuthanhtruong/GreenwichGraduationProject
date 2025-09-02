package com.example.demo.deputyStaff.service;

import com.example.demo.deputyStaff.dao.DeputyStaffsDAO;
import com.example.demo.deputyStaff.model.DeputyStaffs;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
@Service
public class DeputyStaffsServiceImpl implements DeputyStaffsService {
    private final DeputyStaffsDAO deputyStaffsDAO;

    public DeputyStaffsServiceImpl(DeputyStaffsDAO deputyStaffsDAO) {
        this.deputyStaffsDAO = deputyStaffsDAO;
    }

    @Override
    public List<DeputyStaffs> getDeputyStaffs() {
        return deputyStaffsDAO.getDeputyStaffs();
    }

    @Override
    public List<DeputyStaffs> getPaginatedDeputyStaffs(int firstResult, int pageSize) {
        return deputyStaffsDAO.getPaginatedDeputyStaffs(firstResult, pageSize);
    }

    @Override
    public long numberOfDeputyStaffs() {
        return deputyStaffsDAO.numberOfDeputyStaffs();
    }

    @Override
    public void addDeputyStaff(DeputyStaffs deputyStaff, String randomPassword) {
        deputyStaffsDAO.addDeputyStaff(deputyStaff, randomPassword);
    }

    @Override
    public void deleteDeputyStaff(String id) {
        deputyStaffsDAO.deleteDeputyStaff(id);
    }

    @Override
    public DeputyStaffs getDeputyStaffById(String id) {
        return deputyStaffsDAO.getDeputyStaffById(id);
    }

    @Override
    public void editDeputyStaff(DeputyStaffs deputyStaff, MultipartFile avatarFile) throws IOException {
        deputyStaffsDAO.editDeputyStaff(deputyStaff, avatarFile);
    }

    @Override
    public String generateRandomPassword(int length) {
        return deputyStaffsDAO.generateRandomPassword(length);
    }

    @Override
    public String generateUniqueDeputyStaffId(String majorId, LocalDate createdDate) {
        return deputyStaffsDAO.generateUniqueDeputyStaffId(majorId, createdDate);
    }

    @Override
    public List<String> validateDeputyStaff(DeputyStaffs deputyStaff, MultipartFile avatarFile, String majorId, String campusId) {
        return deputyStaffsDAO.validateDeputyStaff(deputyStaff, avatarFile, majorId, campusId);
    }

    @Override
    public List<DeputyStaffs> searchDeputyStaffs(String searchType, String keyword, int firstResult, int pageSize) {
        return deputyStaffsDAO.searchDeputyStaffs(searchType, keyword, firstResult, pageSize);
    }

    @Override
    public long countSearchResults(String searchType, String keyword) {
        return deputyStaffsDAO.countSearchResults(searchType, keyword);
    }
}
