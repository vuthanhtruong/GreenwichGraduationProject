package com.example.demo.service;

import com.example.demo.dto.EmployesDTO;
import com.example.demo.entity.Employes;
import com.example.demo.entity.Majors;
import com.example.demo.entity.Rooms;

import java.util.List;

public interface EmployesService {
    Employes getById(String id);
    List<Rooms> getAll();
    Majors getMajors(EmployesDTO employesDTO);
}
