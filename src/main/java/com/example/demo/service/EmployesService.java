package com.example.demo.service;


import com.example.demo.entity.Employes;
import com.example.demo.entity.AbstractClasses.Rooms;

import java.util.List;

public interface EmployesService {
    Employes getById(String id);
    List<Rooms> getAll();

}
