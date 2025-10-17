package com.example.demo.user.employe.dao;

import com.example.demo.user.employe.model.MajorEmployes;
import com.example.demo.room.model.Rooms;
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
    public MajorEmployes getById(String id) {
        return null;
    }

    @Override
    public List<Rooms> getAll() {
        return List.of();
    }

}
