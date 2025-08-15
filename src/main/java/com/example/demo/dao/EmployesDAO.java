package com.example.demo.dao;


import com.example.demo.entity.AbstractClasses.MajorEmployes;
import com.example.demo.entity.AbstractClasses.Rooms;

import java.util.List;

public interface EmployesDAO {
    MajorEmployes getById(String id);
    List<Rooms> getAll();
}
