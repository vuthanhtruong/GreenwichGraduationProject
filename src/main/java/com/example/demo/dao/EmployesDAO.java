package com.example.demo.dao;


import com.example.demo.entity.Employes;
import com.example.demo.entity.Majors;
import com.example.demo.entity.Rooms;

import java.util.List;

public interface EmployesDAO {
    Employes getById(String id);
    List<Rooms> getAll();
}
