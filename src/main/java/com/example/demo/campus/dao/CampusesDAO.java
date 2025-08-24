package com.example.demo.campus.dao;

import com.example.demo.campus.model.Campuses;

import java.util.List;

public interface CampusesDAO {
    List<Campuses> getCampuses();
    Campuses getCampusById(String campusId);
}
