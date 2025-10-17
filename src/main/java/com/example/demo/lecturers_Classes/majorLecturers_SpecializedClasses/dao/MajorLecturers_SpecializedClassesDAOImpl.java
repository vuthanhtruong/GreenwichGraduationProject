package com.example.demo.lecturers_Classes.majorLecturers_SpecializedClasses.dao;

import com.example.demo.classes.specializedClasses.model.SpecializedClasses;
import com.example.demo.user.majorLecturer.model.MajorLecturers;
import com.example.demo.user.majorLecturer.service.MajorLecturersService;
import com.example.demo.lecturers_Classes.majorLecturers_MajorClasses.model.LecturersClassesId;
import com.example.demo.lecturers_Classes.majorLecturers_SpecializedClasses.model.MajorLecturers_SpecializedClasses;
import com.example.demo.user.staff.service.StaffsService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@Transactional
public class MajorLecturers_SpecializedClassesDAOImpl implements MajorLecturers_SpecializedClassesDAO {
    @Override
    public List<MajorLecturers_SpecializedClasses> getClassByLecturer(MajorLecturers lecturers) {
        return entityManager.createQuery("from MajorLecturers_SpecializedClasses ms where ms.majorLecturer=:lecturers",MajorLecturers_SpecializedClasses.class).
                setParameter("lecturers",lecturers).getResultList();
    }

    @PersistenceContext
    private EntityManager entityManager;

    private final MajorLecturersService lecturersService;
    private final StaffsService staffsService;

    public MajorLecturers_SpecializedClassesDAOImpl(MajorLecturersService lecturersService, StaffsService staffsService) {
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
                        "SELECT lc.majorLecturer FROM MajorLecturers_SpecializedClasses lc WHERE lc.specializedClass = :class AND lc.majorLecturer.majorManagement = :major And lc.majorLecturer.campus=:campus",
                        MajorLecturers.class)
                .setParameter("class", classes)
                .setParameter("major", staffsService.getStaffMajor())
                .setParameter("campus", staffsService.getCampusOfStaff())
                .getResultList();
    }

    @Override
    public List<MajorLecturers> listLecturersNotInClass(SpecializedClasses classes) {
        if (classes == null || classes.getSpecializedSubject() == null || staffsService.getStaffMajor() == null) {
            return List.of();
        }
        try {
            return entityManager.createQuery(
                            "SELECT l FROM MajorLecturers l " +
                                    "WHERE l.majorManagement = :major " +
                                    // loại bỏ giảng viên đã có trong lớp chuyên ngành này
                                    "AND NOT EXISTS (" +
                                    "    SELECT 1 FROM MajorLecturers_SpecializedClasses lc " +
                                    "    WHERE lc.majorLecturer = l " +
                                    "    AND lc.specializedClass = :class" +
                                    ") " +
                                    // chỉ lấy giảng viên đã thuộc chuyên ngành tương ứng
                                    "AND EXISTS (" +
                                    "    SELECT 1 FROM MajorLecturers_Specializations mls " +
                                    "    WHERE mls.majorLecturer = l " +
                                    "    AND mls.specialization.id = :specializationId AND mls.majorLecturer.campus=:campus" +
                                    ")",
                            MajorLecturers.class)
                    .setParameter("major", staffsService.getStaffMajor())
                    .setParameter("campus", staffsService.getCampusOfStaff())
                    .setParameter("class", classes)
                    .setParameter("specializationId", classes.getSpecializedSubject().getSpecialization().getSpecializationId())
                    .getResultList();
        } catch (Exception e) {
            return List.of();
        }
    }

}