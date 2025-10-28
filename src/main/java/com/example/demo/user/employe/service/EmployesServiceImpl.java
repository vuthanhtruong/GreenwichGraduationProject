package com.example.demo.user.employe.service;
import com.example.demo.room.service.RoomsService;
import com.example.demo.user.employe.dao.EmployesDAO;
import com.example.demo.user.employe.model.MajorEmployes;
import com.example.demo.room.model.Rooms;
import com.example.demo.user.employe.model.MinorEmployes;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployesServiceImpl implements EmployesService {
    @Override
    public MinorEmployes getByMinorId(String id) {
        return employesDAO.getByMinorId(id);
    }

    @Override
    public MinorEmployes getMinorEmployee() {
        return employesDAO.getMinorEmployee();
    }

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
