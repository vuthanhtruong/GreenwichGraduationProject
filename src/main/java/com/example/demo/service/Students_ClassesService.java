package com.example.demo.service;

import com.example.demo.entity.Classes;
import com.example.demo.entity.Students;
import com.example.demo.entity.Students_Classes;

import java.util.List;

public interface Students_ClassesService {
    List<Students_Classes> listStudentsInClass(Classes classes);
    List<Students> listStudentsNotInClass(Classes classes);
}
