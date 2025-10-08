package com.example.demo.campus.service;

import com.example.demo.campus.dao.CampusesDAO;
import com.example.demo.campus.model.Campuses;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class CampusesServiceImpl implements CampusesService{
    @Override
    public List<Campuses> listOfExceptionFieldsCampus() {
        return campusesDAO.listOfExceptionFieldsCampus();
    }

    @Override
    public List<Campuses> existsCampusByName(String campusName) {
        return campusesDAO.existsCampusByName(campusName);
    }

    @Override
    public List<String> validateCampus(Campuses campus, MultipartFile avatarFile) {
        return campusesDAO.validateCampus(campus, avatarFile);
    }

    @Override
    public Map<String, Map<String, Long>> getCampusCounts() {
        return campusesDAO.getCampusCounts();
    }

    @Override
    public void updateCampusFields(Campuses existing, Campuses updated) {
        campusesDAO.updateCampusFields(existing, updated);
    }

    @Override
    public String generateUniqueCampusId(LocalDate createdDate) {
        return campusesDAO.generateUniqueCampusId(createdDate);
    }

    @Override
    public List<String> validateCampus(Campuses campus) {
        return campusesDAO.validateCampus(campus);
    }

    @Override
    public void addCampus(Campuses campus) {
        campusesDAO.addCampus(campus);
    }

    @Override
    public boolean existsCampusById(String campusId) {
        return campusesDAO.existsCampusById(campusId);
    }

    @Override
    public void deleteCampus(String campusId) {
        campusesDAO.deleteCampus(campusId);
    }

    @Override
    public void editCampus(Campuses campus) {
        campusesDAO.editCampus(campus);
    }

    @Override
    public Campuses getCampusById(String campusId) {
        return campusesDAO.getCampusById(campusId);
    }

    private final CampusesDAO campusesDAO;

    public CampusesServiceImpl(CampusesDAO campusesDAO) {
        this.campusesDAO = campusesDAO;
    }

    @Override
    public List<Campuses> getCampuses() {
        return campusesDAO.getCampuses();
    }
}
