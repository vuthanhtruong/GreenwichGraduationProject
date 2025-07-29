package com.example.demo.dao.impl;

import com.example.demo.dao.Lecturers_ClassesDAO;
import com.example.demo.entity.Classes;
import com.example.demo.entity.Lecturers;
import com.example.demo.entity.LecturersClassesId;
import com.example.demo.entity.Lecturers_Classes;
import com.example.demo.service.LecturesService;
import com.example.demo.service.PersonsService;
import com.example.demo.service.StaffsService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@Transactional
public class Lecturers_ClassesDAOImpl implements Lecturers_ClassesDAO {

    private final PersonsService personsService;
    private final LecturesService lecturesService;
    private  final StaffsService staffsService;

    @Override
    public void addLecturersToClass(Classes classes, List<String> lecturerIds) {
        for (String lecturerId : lecturerIds) {
            Lecturers lecturer = lecturesService.getLecturerById(lecturerId);
            Lecturers_Classes lecturerClass = new Lecturers_Classes();
            LecturersClassesId id = new LecturersClassesId(lecturerId, classes.getClassId());
            lecturerClass.setId(id);
            lecturerClass.setClassEntity(classes);
            lecturerClass.setLecturer(lecturer);
            lecturerClass.setCreatedAt(LocalDateTime.now());
            lecturerClass.setAddedBy(staffsService.getStaffs());
            entityManager.persist(lecturerClass);
        }
    }

    @PersistenceContext
    private EntityManager entityManager;


    public Lecturers_ClassesDAOImpl(PersonsService personsService, LecturesService lecturesService, StaffsService staffsService) {
        this.personsService = personsService;
        this.lecturesService = lecturesService;
        this.staffsService = staffsService;
    }

    @Override
    public List<Lecturers_Classes> listLecturersInClass(Classes classes) {
        return entityManager.createQuery(
                        "SELECT lc FROM Lecturers_Classes lc WHERE lc.classEntity = :class AND lc.lecturer.majorManagement = :major",
                        Lecturers_Classes.class)
                .setParameter("class", classes)
                .setParameter("major", staffsService.getMajors())
                .getResultList();
    }

    @Override
    public List<Lecturers> listLecturersNotInClass(Classes classes) {
        return entityManager.createQuery(
                        "SELECT l FROM Lecturers l WHERE l.majorManagement = :major AND l.id NOT IN " +
                                "(SELECT lc.lecturer.id FROM Lecturers_Classes lc WHERE lc.classEntity = :class)",
                        Lecturers.class)
                .setParameter("class", classes)
                .setParameter("major", staffsService.getMajors())
                .getResultList();
    }

}