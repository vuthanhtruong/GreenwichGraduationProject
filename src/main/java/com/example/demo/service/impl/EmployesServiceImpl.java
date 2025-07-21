package com.example.demo.service.impl;

import com.example.demo.dao.EmployesDAO;
import com.example.demo.dao.StaffsDAO;
import com.example.demo.dto.EmployesDTO;
import com.example.demo.dto.MajorsDTO;
import com.example.demo.dto.RoomsDTO;
import com.example.demo.entity.Employes;
import com.example.demo.entity.Majors;
import com.example.demo.entity.Rooms;
import com.example.demo.service.EmployesService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployesServiceImpl implements EmployesService {
    @Override
    public Employes getById(String id) {
        return null;
    }

    @Override
    public List<Rooms> getAll() {
        return List.of();
    }

    @Override
    public Majors getMajors(EmployesDTO employesDTO) {
        return null;
    }
}
