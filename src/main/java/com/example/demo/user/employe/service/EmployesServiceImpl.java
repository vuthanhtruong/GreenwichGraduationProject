package com.example.demo.user.employe.service;
import com.example.demo.user.employe.model.MajorEmployes;
import com.example.demo.room.model.Rooms;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployesServiceImpl implements EmployesService {
    @Override
    public MajorEmployes getById(String id) {
        return null;
    }

    @Override
    public List<Rooms> getAll() {
        return List.of();
    }

}
