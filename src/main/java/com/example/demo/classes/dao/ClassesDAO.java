package com.example.demo.classes.dao;

import com.example.demo.classes.model.Classes;

public interface ClassesDAO {
    Classes findClassById(String classId);
}
