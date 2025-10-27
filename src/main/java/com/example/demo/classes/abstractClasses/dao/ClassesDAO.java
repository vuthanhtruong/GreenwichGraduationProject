package com.example.demo.classes.abstractClasses.dao;

import com.example.demo.classes.abstractClasses.model.Classes;

public interface ClassesDAO {
    Classes findClassById(String classId);
}
