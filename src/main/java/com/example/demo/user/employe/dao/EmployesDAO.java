package com.example.demo.user.employe.dao;


import com.example.demo.user.employe.model.MajorEmployes;
import com.example.demo.room.model.Rooms;

import java.util.List;

public interface EmployesDAO {
    MajorEmployes getById(String id);
    List<Rooms> getAll();
}
