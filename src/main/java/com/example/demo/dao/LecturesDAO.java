package com.example.demo.dao;

import com.example.demo.dto.RoomsDTO;
import com.example.demo.dto.StudentsDTO;

import java.util.List;

public interface LecturesDAO {
    List<RoomsDTO> getAll();
    List<StudentsDTO> getStudents();
}
