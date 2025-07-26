package com.example.demo.service.impl;

import com.example.demo.dao.Students_ClassesDAO;
import com.example.demo.entity.Classes;
import com.example.demo.entity.Students;
import com.example.demo.entity.Students_Classes;
import com.example.demo.service.Students_ClassesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Students_ClassesServiceImpl implements Students_ClassesService {
    @Override
    public List<Students> listStudentsNotInClass(Classes classes) {
        return students_ClassesDAO.listStudentsNotInClass(classes);
    }

    @Override
    public List<Students_Classes> listStudentsInClass(Classes classes) {
        return students_ClassesDAO.listStudentsInClass(classes);
    }

    private Students_ClassesDAO students_ClassesDAO;
    public Students_ClassesServiceImpl(Students_ClassesDAO students_ClassesDAO) {
        this.students_ClassesDAO = students_ClassesDAO;
    }

}
