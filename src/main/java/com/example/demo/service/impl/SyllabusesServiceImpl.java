package com.example.demo.service.impl;

import com.example.demo.dao.SyllabusesDAO;
import com.example.demo.entity.MajorSubjects;
import com.example.demo.entity.Syllabuses;
import com.example.demo.service.SyllabusesService;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class SyllabusesServiceImpl implements SyllabusesService {
    @Override
    public void deleteSyllabusBySubject(MajorSubjects subject) {
        syllabusesDAO.deleteSyllabusBySubject(subject);
    }

    @Override
    public Syllabuses getSyllabusById(String syllabusId) {
        return syllabusesDAO.getSyllabusById(syllabusId);
    }

    @Override
    public void addSyllabus(Syllabuses syllabus) {
        syllabusesDAO.addSyllabus(syllabus);
    }

    private SyllabusesDAO syllabusesDAO;

    public SyllabusesServiceImpl(SyllabusesDAO syllabusesDAO) {
        this.syllabusesDAO = syllabusesDAO;
    }

    @Override
    public List<Syllabuses> syllabusesList() {
        return syllabusesDAO.syllabusesList();
    }

    @Override
    public List<Syllabuses> getSyllabusesBySubject(MajorSubjects subject) {
        return syllabusesDAO.getSyllabusesBySubject(subject);
    }
}
