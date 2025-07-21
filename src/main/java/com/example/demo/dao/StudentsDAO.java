package com.example.demo.dao;


import com.example.demo.entity.Classes;
import com.example.demo.entity.Rooms;
import com.example.demo.entity.Students;

import java.util.List;

public interface StudentsDAO {
    Students getById(int id);
    List<Classes> getClasses();
    List<Rooms> getRooms();
}
