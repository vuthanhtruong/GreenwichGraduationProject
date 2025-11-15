package com.example.demo.lecturers_Classes.minorLecturers_MinorClasses.dao;

import com.example.demo.classes.minorClasses.model.MinorClasses;
import com.example.demo.lecturers_Classes.abstractLecturers_Classes.model.LecturersClassesId;
import com.example.demo.lecturers_Classes.minorLecturers_MinorClasses.model.MinorLecturers_MinorClasses;
import com.example.demo.user.deputyStaff.model.DeputyStaffs;
import com.example.demo.user.deputyStaff.service.DeputyStaffsService;
import com.example.demo.user.minorLecturer.model.MinorLecturers;
import com.example.demo.user.minorLecturer.service.MinorLecturersService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Repository
@Transactional
public class MinorLecturers_MinorClassesDAOImpl implements MinorLecturers_MinorClassesDAO {

    @Override
    public List<String> getClassNotificationsForLecturer(String lecturerId) {
        String jpql = """
        SELECT CONCAT('You have been added to minor class: ', 
                      c.nameClass, ' (', 
                      COALESCE(c.minorSubject.subjectName, 'N/A'), 
                      ') on ', lmc.createdAt)
        FROM MinorLecturers_MinorClasses lmc
        JOIN lmc.minorClass c
        WHERE lmc.lecturer.id = :lecturerId
          AND lmc.notificationType = 'NOTIFICATION_003'
        """;

        return entityManager.createQuery(jpql, String.class)
                .setParameter("lecturerId", lecturerId)
                .getResultList();
    }

    @PersistenceContext
    private EntityManager entityManager;

    private final MinorLecturersService minorLecturersService;
    private final DeputyStaffsService deputyStaffsService;

    public MinorLecturers_MinorClassesDAOImpl(
            MinorLecturersService minorLecturersService,
            DeputyStaffsService deputyStaffsService) {
        this.minorLecturersService = minorLecturersService;
        this.deputyStaffsService = deputyStaffsService;
    }

    @Override
    public List<MinorLecturers_MinorClasses> getClassByLecturer(MinorLecturers lecturer) {
        if (lecturer == null) return Collections.emptyList();
        return entityManager.createQuery(
                        "FROM MinorLecturers_MinorClasses mlmc WHERE mlmc.lecturer = :lecturer",
                        MinorLecturers_MinorClasses.class)
                .setParameter("lecturer", lecturer)
                .getResultList();
    }

    @Override
    public void addLecturersToClass(MinorClasses minorClass, List<String> lecturerIds) {
        if (minorClass == null || lecturerIds == null || lecturerIds.isEmpty()) return;

        DeputyStaffs addedBy = deputyStaffsService.getDeputyStaff();

        for (String lecturerId : lecturerIds) {
            MinorLecturers lecturer = minorLecturersService.getMinorLecturerById(lecturerId);
            if (lecturer == null) continue;

            LecturersClassesId id = new LecturersClassesId(lecturerId, minorClass.getClassId());
            MinorLecturers_MinorClasses link = new MinorLecturers_MinorClasses();
            link.setId(id);
            link.setLecturer(lecturer);
            link.setMinorClass(minorClass);
            link.setCreatedAt(LocalDateTime.now());
            link.setAddedBy(addedBy);
            entityManager.persist(link);
        }
    }

    @Override
    public void removeLecturerFromClass(MinorClasses minorClass, List<String> lecturerIds) {
        if (minorClass == null || lecturerIds == null || lecturerIds.isEmpty()) return;

        for (String lecturerId : lecturerIds) {
            LecturersClassesId id = new LecturersClassesId(lecturerId, minorClass.getClassId());
            MinorLecturers_MinorClasses link = entityManager.find(MinorLecturers_MinorClasses.class, id);
            if (link != null) {
                entityManager.remove(link);
            }
        }
    }

    @Override
    public List<MinorLecturers> listLecturersInClass(MinorClasses minorClass) {
        if (minorClass == null) return Collections.emptyList();
        return entityManager.createQuery(
                        "SELECT mlmc.lecturer FROM MinorLecturers_MinorClasses mlmc WHERE mlmc.minorClass = :minorClass",
                        MinorLecturers.class)
                .setParameter("minorClass", minorClass)
                .getResultList();
    }

    // ĐÃ SỬA: KHÔNG DÙNG SPECIALIZATION
    @Override
    public List<MinorLecturers> listLecturersNotInClass(MinorClasses minorClass) {
        if (minorClass == null) return Collections.emptyList();

        String campus = deputyStaffsService.getCampus().getCampusId();

        try {
            return entityManager.createQuery(
                            "SELECT l FROM MinorLecturers l " +
                                    "WHERE l.campus.campusId = :campus " +
                                    "AND l.id NOT IN (" +
                                    "    SELECT mlmc.lecturer.id FROM MinorLecturers_MinorClasses mlmc " +
                                    "    WHERE mlmc.minorClass = :minorClass" +
                                    ")",
                            MinorLecturers.class)
                    .setParameter("campus", campus)
                    .setParameter("minorClass", minorClass)
                    .getResultList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}