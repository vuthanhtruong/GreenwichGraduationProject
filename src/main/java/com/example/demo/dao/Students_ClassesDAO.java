package com.example.demo.dao;

import com.example.demo.entity.Classes;
import com.example.demo.entity.Students;
import com.example.demo.entity.Students_Classes;

import java.util.List;

public interface Students_ClassesDAO {
    List<Students_Classes> listStudentsInClass(Classes classes);
    List<Students> listStudentsNotInClass(Classes classes);
}
