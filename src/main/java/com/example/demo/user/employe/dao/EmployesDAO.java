package com.example.demo.user.employe.dao;


import com.example.demo.user.employe.model.MajorEmployes;
import com.example.demo.room.model.Rooms;
import com.example.demo.user.employe.model.MinorEmployes;

import java.util.List;

public interface EmployesDAO {
    MajorEmployes getById(String id);
    MinorEmployes getByMinorId(String id);
    MajorEmployes getMajorEmployee();
    MinorEmployes getMinorEmployee();
    List<Rooms> getAll();
}
