package com.example.demo.dao;

import java.util.List;

public interface ClassesDAO {
    List<Class> getClasses();
    Class getClassById(int id);
    Class getClassByName(String name);
    Class addClass(Class c);
    Class updateClass(Class c);
    void deleteClass(int id);
}
