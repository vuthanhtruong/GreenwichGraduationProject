package com.example.demo.user.employe.service;


import com.example.demo.user.employe.model.MajorEmployes;
import com.example.demo.room.model.Rooms;

import java.util.List;

public interface EmployesService {
    MajorEmployes getById(String id);
    List<Rooms> getAll();

}
