package com.example.demo.user.employe.service;
import com.example.demo.room.service.RoomsService;
import com.example.demo.user.employe.dao.EmployesDAO;
import com.example.demo.user.employe.model.MajorEmployes;
import com.example.demo.room.model.Rooms;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployesServiceImpl implements EmployesService {
    private final EmployesDAO employesDAO;

    public EmployesServiceImpl(EmployesDAO employesDAO) {
        this.employesDAO = employesDAO;
    }


    @Override
    public MajorEmployes getMajorEmployee() {
        return employesDAO.getMajorEmployee();
    }

    @Override
    public MajorEmployes getById(String id) {
        return employesDAO.getById(id);
    }

    @Override
    public List<Rooms> getAll() {
        return List.of();
    }

}
