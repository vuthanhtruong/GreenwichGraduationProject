package com.example.demo.campus.service;

import com.example.demo.campus.model.Campuses;

import java.util.List;

public interface CampusesService {
    List<Campuses> getCampuses();
    Campuses getCampusById(String campusId);
}
