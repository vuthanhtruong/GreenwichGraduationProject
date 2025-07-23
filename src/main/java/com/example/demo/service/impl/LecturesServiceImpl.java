package com.example.demo.service.impl;

import com.example.demo.dao.LecturesDAO;
import com.example.demo.entity.Lecturers;
import com.example.demo.service.LecturesService;
import jakarta.mail.MessagingException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LecturesServiceImpl implements LecturesService {

    private final LecturesDAO lecturesDAO;

    public LecturesServiceImpl(LecturesDAO lecturesDAO) {
        this.lecturesDAO = lecturesDAO;
    }

    @Override
    public List<Lecturers> getLecturers() {
        return lecturesDAO.getLecturers();
    }

    @Override
    public Lecturers addLecturers(Lecturers lecturers, String randomPassword) {
        return lecturesDAO.addLecturers(lecturers, randomPassword);
    }

    @Override
    public long numberOfLecturers() {
        return lecturesDAO.numberOfLecturers();
    }

    @Override
    public void deleteLecturer(String id) {
        lecturesDAO.deleteLecturer(id);
    }

    @Override
    public void updateLecturer(String id, Lecturers lecturer) throws MessagingException {
        lecturesDAO.updateLecturer(id, lecturer);
    }

    @Override
    public Lecturers getLecturerById(String id) {
        return lecturesDAO.getLecturerById(id);
    }

    @Override
    public List<Lecturers> getPaginatedLecturers(int firstResult, int pageSize) {
        return lecturesDAO.getPaginatedLecturers(firstResult, pageSize);
    }
}