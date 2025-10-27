package com.example.demo.lecturers_Classes.abstractLecturers_Classes.dao;

import com.example.demo.lecturers_Classes.abstractLecturers_Classes.model.Lecturers_Classes;
import com.example.demo.lecturers_Classes.majorLecturers_MajorClasses.service.MajorLecturers_MajorClassesService;
import com.example.demo.lecturers_Classes.majorLecturers_SpecializedClasses.service.MajorLecturers_SpecializedClassesService;
import com.example.demo.user.majorLecturer.model.MajorLecturers;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@Transactional
public class Lecturers_ClassesDAOImpl implements Lecturers_ClassesDAO {
    private final MajorLecturers_MajorClassesService majorDAO;
    private final MajorLecturers_SpecializedClassesService specDAO;
    @PersistenceContext
    private EntityManager entityManager;

    public Lecturers_ClassesDAOImpl(MajorLecturers_MajorClassesService majorDAO, MajorLecturers_SpecializedClassesService specDAO) {
        this.majorDAO = majorDAO;
        this.specDAO = specDAO;
    }

    @Override
    public List<Lecturers_Classes> getLecturers_ClassesByClassId(String classId) {
        return entityManager.createQuery("from Lecturers_Classes lc where lc.classEntity.classId=:classId", Lecturers_Classes.class).setParameter("classId", classId).getResultList();
    }
    @Override
    public List<Lecturers_Classes> getClassesByLecturer(MajorLecturers lecturer) {
        List<Lecturers_Classes> result = new ArrayList<>();
        result.addAll(majorDAO.getClassByLecturer(lecturer));
        result.addAll(specDAO.getClassByLecturer(lecturer));
        return result;
    }

    @Override
    public List<Lecturers_Classes> getClassesByLecturer(MajorLecturers lecturer, int firstResult, int pageSize) {
        return getClassesByLecturer(lecturer).stream()
                .skip(firstResult)
                .limit(pageSize)
                .collect(Collectors.toList());
    }

    @Override
    public long countClassesByLecturer(MajorLecturers lecturer) {
        return getClassesByLecturer(lecturer).size();
    }
}
