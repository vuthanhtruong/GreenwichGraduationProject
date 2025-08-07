package com.example.demo.dao.impl;

import com.example.demo.dao.EmployesDAO;

import com.example.demo.entity.Employes;
import com.example.demo.entity.Majors;
import com.example.demo.entity.Rooms;
import com.example.demo.entity.Students;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class EmployesDAOImpl implements EmployesDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Employes getById(String id) {
        return null;
    }

    @Override
    public List<Rooms> getAll() {
        return List.of();
    }

}
