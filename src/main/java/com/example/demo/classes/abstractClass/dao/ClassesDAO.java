package com.example.demo.classes.abstractClass.dao;

import com.example.demo.classes.abstractClass.model.Classes;

public interface ClassesDAO {
    Classes findClassById(String classId);
}
