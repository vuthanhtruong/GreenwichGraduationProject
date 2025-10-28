package com.example.demo.user.employe.service;


import com.example.demo.user.employe.model.MajorEmployes;
import com.example.demo.room.model.Rooms;
import com.example.demo.user.employe.model.MinorEmployes;

import java.util.List;

public interface EmployesService {
    MajorEmployes getById(String id);
    MinorEmployes getByMinorId(String id);
    MajorEmployes getMajorEmployee();
    MinorEmployes getMinorEmployee();
    List<Rooms> getAll();
}
