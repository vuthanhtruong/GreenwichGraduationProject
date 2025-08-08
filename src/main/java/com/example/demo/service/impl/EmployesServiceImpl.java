package com.example.demo.service.impl;
import com.example.demo.entity.Employes;
import com.example.demo.entity.AbstractClasses.Rooms;
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

}
