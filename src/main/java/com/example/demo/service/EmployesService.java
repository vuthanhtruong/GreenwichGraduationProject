package com.example.demo.service;


import com.example.demo.entity.AbstractClasses.MajorEmployes;
import com.example.demo.entity.AbstractClasses.Rooms;

import java.util.List;

public interface EmployesService {
    MajorEmployes getById(String id);
    List<Rooms> getAll();

}
