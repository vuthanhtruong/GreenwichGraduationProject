package com.example.demo.dao.impl;

import com.example.demo.dao.Lecturers_ClassesDAO;
import com.example.demo.entity.MajorClasses;
import com.example.demo.entity.MajorLecturers;
import com.example.demo.entity.LecturersClassesId;
import com.example.demo.entity.Lecturers_MajorClasses;
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
    @Override
    public void removeLecturerFromClass(MajorClasses classes, List<String> lecturerIds) {
        if (classes == null || lecturerIds == null || lecturerIds.isEmpty()) {
            return; // No action if inputs are invalid
        }

        for (String lecturerId : lecturerIds) {
            // Find the Lecturers_Classes record by composite key
            LecturersClassesId id = new LecturersClassesId(lecturerId, classes.getClassId());
            Lecturers_MajorClasses lecturerClass = entityManager.find(Lecturers_MajorClasses.class, id);
            if (lecturerClass != null) {
                entityManager.remove(lecturerClass); // Delete the record
            }
        }
    }

    private final PersonsService personsService;
    private final LecturesService lecturesService;
    private  final StaffsService staffsService;

    @Override
    public void addLecturersToClass(MajorClasses classes, List<String> lecturerIds) {
        for (String lecturerId : lecturerIds) {
            MajorLecturers lecturer = lecturesService.getLecturerById(lecturerId);
            Lecturers_MajorClasses lecturerClass = new Lecturers_MajorClasses();
            LecturersClassesId id = new LecturersClassesId(lecturerId, classes.getClassId());
            lecturerClass.setId(id);
            lecturerClass.setClassEntity(classes);
            lecturerClass.setLecturer(lecturer);
            lecturerClass.setCreatedAt(LocalDateTime.now());
            lecturerClass.setAddedBy(staffsService.getStaff());
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
    public List<Lecturers_MajorClasses> listLecturersInClass(MajorClasses classes) {
        return entityManager.createQuery(
                        "SELECT lc FROM Lecturers_MajorClasses lc WHERE lc.classEntity = :class AND lc.lecturer.majorManagement = :major",
                        Lecturers_MajorClasses.class)
                .setParameter("class", classes)
                .setParameter("major", staffsService.getStaffMajor())
                .getResultList();
    }

    @Override
    public List<MajorLecturers> listLecturersNotInClass(MajorClasses classes) {
        return entityManager.createQuery(
                        "SELECT l FROM MajorLecturers l WHERE l.majorManagement = :major AND l.id NOT IN " +
                                "(SELECT lc.lecturer.id FROM Lecturers_MajorClasses lc WHERE lc.classEntity = :class)",
                        MajorLecturers.class)
                .setParameter("class", classes)
                .setParameter("major", staffsService.getStaffMajor())
                .getResultList();
    }

}