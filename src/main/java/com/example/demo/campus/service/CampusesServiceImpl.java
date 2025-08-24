package com.example.demo.campus.service;

import com.example.demo.campus.dao.CampusesDAO;
import com.example.demo.campus.model.Campuses;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class CampusesServiceImpl implements CampusesService{
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
