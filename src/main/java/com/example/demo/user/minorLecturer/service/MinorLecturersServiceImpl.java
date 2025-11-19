package com.example.demo.user.minorLecturer.service;

import com.example.demo.user.minorLecturer.dao.MinorLecturersDAO;
import com.example.demo.user.minorLecturer.model.MinorLecturers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class MinorLecturersServiceImpl implements MinorLecturersService {
    @Override
    public long totalMinorLecturersAllCampus() {
        return minorLecturersDAO.totalMinorLecturersAllCampus();
    }

    @Override
    public long newMinorLecturersThisYearAllCampus() {
        return minorLecturersDAO.newMinorLecturersThisYearAllCampus();
    }

    @Override
    public Map<String, Long> minorLecturersByCampus() {
        return minorLecturersDAO.minorLecturersByCampus();
    }

    @Override
    public Map<String, Long> minorLecturersByGender() {
        return minorLecturersDAO.minorLecturersByGender();
    }

    @Override
    public Map<String, Long> minorLecturersByAgeGroup() {
        return minorLecturersDAO.minorLecturersByAgeGroup();
    }

    @Override
    public List<MinorLecturers> top5NewestMinorLecturers() {
        return minorLecturersDAO.top5NewestMinorLecturers();
    }

    @Override
    public List<MinorLecturers> top5MostSeniorMinorLecturers() {
        return minorLecturersDAO.top5MostSeniorMinorLecturers();
    }

    @Override
    public long countCampusesWithoutMinorLecturer() {
        return minorLecturersDAO.countCampusesWithoutMinorLecturer();
    }

    @Override
    public long totalMinorLecturersInMyCampus() {
        return minorLecturersDAO.totalMinorLecturersInMyCampus();
    }

    @Override
    public long newMinorLecturersThisYearInMyCampus() {
        return minorLecturersDAO.newMinorLecturersThisYearInMyCampus();
    }

    @Override
    public List<Object[]> minorLecturersByGenderInMyCampus() {
        return minorLecturersDAO.minorLecturersByGenderInMyCampus();
    }

    @Override
    public List<Object[]> minorLecturersByAgeGroupInMyCampus() {
        return minorLecturersDAO.minorLecturersByAgeGroupInMyCampus();
    }

    @Override
    public List<Object[]> top5MostExperiencedMinorLecturersInMyCampus() {
        return List.of();
    }

    @Override
    public List<MinorLecturers> colleaguesByCampusId(String campusId) {
        return minorLecturersDAO.colleaguesByCampusId(campusId);
    }

    @Override
    public List<MinorLecturers> searchMinorLecturersByCampus(String campusId, String searchType, String keyword, int firstResult, int pageSize) {
        return minorLecturersDAO.searchMinorLecturersByCampus(campusId, searchType, keyword, firstResult, pageSize);
    }

    @Override
    public long countSearchMinorLecturersByCampus(String campusId, String searchType, String keyword) {
        return minorLecturersDAO.countSearchMinorLecturersByCampus(campusId, searchType, keyword);
    }

    @Override
    public List<MinorLecturers> colleagueBycampusId(String campusId) {
        return minorLecturersDAO.colleagueBycampusId(campusId);
    }

    @Override
    public MinorLecturers getMinorLecturer() {
        return minorLecturersDAO.getMinorLecturer();
    }

    private final MinorLecturersDAO minorLecturersDAO;

    @Autowired
    public MinorLecturersServiceImpl(MinorLecturersDAO minorLecturersDAO) {
        this.minorLecturersDAO = minorLecturersDAO;
    }

    @Override
    public List<MinorLecturers> getMinorLecturers() {
        return minorLecturersDAO.getMinorLecturers();
    }

    @Override
    public MinorLecturers addMinorLecturers(MinorLecturers minorLecturer, String randomPassword) {
        return minorLecturersDAO.addMinorLecturers(minorLecturer, randomPassword);
    }

    @Override
    public long numberOfMinorLecturers() {
        return minorLecturersDAO.numberOfMinorLecturers();
    }

    @Override
    public void deleteMinorLecturer(String id) {
        minorLecturersDAO.deleteMinorLecturer(id);
    }

    @Override
    public MinorLecturers getMinorLecturerById(String id) {
        return minorLecturersDAO.getMinorLecturerById(id);
    }

    @Override
    public void updateMinorLecturer(String id, MinorLecturers minorLecturer, MultipartFile avatarFile) throws Exception {
        minorLecturersDAO.updateMinorLecturer(id, minorLecturer, avatarFile);
    }

    @Override
    public String generateRandomPassword(int length) {
        return minorLecturersDAO.generateRandomPassword(length);
    }

    @Override
    public String generateUniqueMinorLectureId(LocalDate createdDate) {
        return minorLecturersDAO.generateUniqueMinorLectureId(createdDate);
    }

    @Override
    public Map<String, String> minorLecturerValidation(MinorLecturers minorLecturer, MultipartFile avatarFile) {
        return minorLecturersDAO.minorLecturerValidation(minorLecturer, avatarFile);
    }

    @Override
    public List<MinorLecturers> getPaginatedMinorLecturers(int firstResult, int pageSize) {
        return minorLecturersDAO.getPaginatedMinorLecturers(firstResult, pageSize);
    }

    @Override
    public List<MinorLecturers> searchMinorLecturers(String searchType, String keyword, int firstResult, int pageSize) {
        return minorLecturersDAO.searchMinorLecturers(searchType, keyword, firstResult, pageSize);
    }

    @Override
    public long countSearchResults(String searchType, String keyword) {
        return minorLecturersDAO.countSearchResults(searchType, keyword);
    }
}