package com.example.demo.lecturer_class.dao;

import com.example.demo.lecturer.model.MajorLecturers;
import com.example.demo.lecturer.service.LecturesService;
import com.example.demo.lecturer_class.model.LecturersClassesId;
import com.example.demo.lecturer_class.model.MajorLecturers_SpecializedClasses;
import com.example.demo.specializedClasses.model.SpecializedClasses;
import com.example.demo.staff.service.StaffsService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@Transactional
public class MajorLecturers_SpecializedClassesDAOImpl implements MajorLecturers_SpecializedClassesDAO {

    @PersistenceContext
    private EntityManager entityManager;

    private final LecturesService lecturersService;
    private final StaffsService staffsService;

    public MajorLecturers_SpecializedClassesDAOImpl(LecturesService lecturersService, StaffsService staffsService) {
        this.lecturersService = lecturersService;
        this.staffsService = staffsService;
    }

    @Override
    public void removeLecturerFromClass(SpecializedClasses classes, List<String> lecturerIds) {
        if (classes == null || lecturerIds == null || lecturerIds.isEmpty()) {
            return;
        }

        for (String lecturerId : lecturerIds) {
            LecturersClassesId id = new LecturersClassesId(lecturerId, classes.getClassId());
            MajorLecturers_SpecializedClasses lecturerClass = entityManager.find(MajorLecturers_SpecializedClasses.class, id);
            if (lecturerClass != null) {
                entityManager.remove(lecturerClass);
            }
        }
    }

    @Override
    public void addLecturersToClass(SpecializedClasses classes, List<String> lecturerIds) {
        if (classes == null || lecturerIds == null || lecturerIds.isEmpty()) {
            return;
        }

        for (String lecturerId : lecturerIds) {
            MajorLecturers lecturer = lecturersService.getLecturerById(lecturerId);
            if (lecturer == null) {
                continue; // Skip if lecturer not found
            }
            MajorLecturers_SpecializedClasses lecturerClass = new MajorLecturers_SpecializedClasses();
            LecturersClassesId id = new LecturersClassesId(lecturerId, classes.getClassId());
            lecturerClass.setId(id);
            lecturerClass.setMajorLecturer(lecturer);
            lecturerClass.setClassEntity(classes);
            lecturerClass.setCreatedAt(LocalDateTime.now());
            lecturerClass.setAddedBy(staffsService.getStaff());
            entityManager.persist(lecturerClass);
        }
    }

    @Override
    public List<MajorLecturers> listLecturersInClass(SpecializedClasses classes) {
        return entityManager.createQuery(
                        "SELECT lc.majorLecturer FROM MajorLecturers_SpecializedClasses lc WHERE lc.specializedClass = :class AND lc.majorLecturer.majorManagement = :major",
                        MajorLecturers.class)
                .setParameter("class", classes)
                .setParameter("major", staffsService.getStaffMajor())
                .getResultList();
    }

    @Override
    public List<MajorLecturers> listLecturersNotInClass(SpecializedClasses classes) {
        return entityManager.createQuery(
                        "SELECT l FROM MajorLecturers l WHERE l.majorManagement = :major " +
                                "AND l.id NOT IN (" +
                                "    SELECT lc.majorLecturer.id FROM MajorLecturers_SpecializedClasses lc WHERE lc.specializedClass = :class" +
                                ") " +
                                "AND l.id IN (" +
                                "    SELECT mls.majorLecturer.id FROM MajorLecturers_Specializations mls WHERE mls.specialization.id = :specializationId" +
                                ")",
                        MajorLecturers.class)
                .setParameter("class", classes)
                .setParameter("major", staffsService.getStaffMajor())
                .setParameter("specializationId", classes.getSpecialization().getSpecializationId())
                .getResultList();
    }
}