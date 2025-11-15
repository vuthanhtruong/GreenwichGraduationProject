package com.example.demo.lecturers_Classes.majorLecturers_MajorClasses.dao;

import com.example.demo.classes.majorClasses.model.MajorClasses;
import com.example.demo.user.majorLecturer.model.MajorLecturers;
import com.example.demo.lecturers_Classes.abstractLecturers_Classes.model.LecturersClassesId;
import com.example.demo.lecturers_Classes.majorLecturers_MajorClasses.model.MajorLecturers_MajorClasses;
import com.example.demo.user.majorLecturer.service.MajorLecturersService;
import com.example.demo.user.staff.service.StaffsService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@Transactional
public class MajorLecturers_MajorClassesDAOImpl implements MajorLecturers_MajorClassesDAO {

    @Override
    public List<String> getClassNotificationsForLecturer(String lecturerId) {
        String jpql = """
        SELECT CONCAT('You have been added to major class: ', 
                      c.nameClass, ' (', 
                      COALESCE(c.subject.subjectName, 'N/A'), 
                      ') on ', lmc.createdAt)
        FROM MajorLecturers_MajorClasses lmc
        JOIN lmc.majorClass c
        WHERE lmc.lecturer.id = :lecturerId
          AND lmc.notificationType = 'NOTIFICATION_002'
        """;

        return entityManager.createQuery(jpql, String.class)
                .setParameter("lecturerId", lecturerId)
                .getResultList();
    }

    @Override
    public List<MajorLecturers_MajorClasses> getClassByLecturer(MajorLecturers lecturers) {
        return entityManager.createQuery("from MajorLecturers_MajorClasses m where m.lecturer=:lecturer",MajorLecturers_MajorClasses.class)
                .setParameter("lecturer",lecturers).getResultList();
    }

    @PersistenceContext
    private EntityManager entityManager;

    private final MajorLecturersService lecturersService;
    private final StaffsService staffsService;

    public MajorLecturers_MajorClassesDAOImpl(MajorLecturersService lecturersService, StaffsService staffsService) {
        this.lecturersService = lecturersService;
        this.staffsService = staffsService;
    }

    @Override
    public void removeLecturerFromClass(MajorClasses classes, List<String> lecturerIds) {
        if (classes == null || lecturerIds == null || lecturerIds.isEmpty()) {
            return;
        }

        for (String lecturerId : lecturerIds) {
            LecturersClassesId id = new LecturersClassesId(lecturerId, classes.getClassId());
            MajorLecturers_MajorClasses lecturerClass = entityManager.find(MajorLecturers_MajorClasses.class, id);
            if (lecturerClass != null) {
                entityManager.remove(lecturerClass);
            }
        }
    }

    @Override
    public void addLecturersToClass(MajorClasses classes, List<String> lecturerIds) {
        for (String lecturerId : lecturerIds) {
            MajorLecturers lecturer = lecturersService.getLecturerById(lecturerId);
            MajorLecturers_MajorClasses lecturerClass = new MajorLecturers_MajorClasses();
            LecturersClassesId id = new LecturersClassesId(lecturerId, classes.getClassId());
            lecturerClass.setId(id);
            lecturerClass.setMajorClass(classes);
            lecturerClass.setLecturer(lecturer);
            lecturerClass.setCreatedAt(LocalDateTime.now());
            lecturerClass.setAddedBy(staffsService.getStaff());
            entityManager.persist(lecturerClass);
        }
    }

    @Override
    public List<MajorLecturers> listLecturersInClass(MajorClasses classes) {
        return entityManager.createQuery(
                        "SELECT lc.lecturer FROM MajorLecturers_MajorClasses lc WHERE lc.majorClass = :class",
                        MajorLecturers.class)
                .setParameter("class", classes)
                .getResultList();
    }

    @Override
    public List<MajorLecturers> listLecturersNotInClass(MajorClasses classes) {
        return entityManager.createQuery(
                        "SELECT l FROM MajorLecturers l WHERE l.majorManagement = :major AND l.id NOT IN " +
                                "(SELECT lc.lecturer.id FROM MajorLecturers_MajorClasses lc WHERE lc.majorClass = :class)",
                        MajorLecturers.class)
                .setParameter("class", classes)
                .setParameter("major", staffsService.getStaffMajor())
                .getResultList();
    }
}