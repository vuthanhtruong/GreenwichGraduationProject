package com.example.demo.lecturers_Classes.majorLecturers_SpecializedClasses.dao;

import com.example.demo.classes.specializedClasses.model.SpecializedClasses;
import com.example.demo.user.majorLecturer.model.MajorLecturers;
import com.example.demo.user.majorLecturer.service.MajorLecturersService;
import com.example.demo.lecturers_Classes.abstractLecturers_Classes.model.LecturersClassesId;
import com.example.demo.lecturers_Classes.majorLecturers_SpecializedClasses.model.MajorLecturers_SpecializedClasses;
import com.example.demo.user.staff.model.Staffs;
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

    // ==================== DASHBOARD SPECIALIZED LECTURERS (CHO MAJOR STAFF) ====================

    /**
     * 1. Tổng số giảng viên đang dạy ít nhất 1 lớp chuyên ngành
     */
    @Override
    public long countLecturersTeachingSpecializedClasses() {
        String jpql = """
        SELECT COUNT(DISTINCT lmc.lecturer.id)
        FROM MajorLecturers_SpecializedClasses lmc
        JOIN lmc.specializedClass sc
        WHERE sc.creator.campus.campusId = :campusId
          AND sc.specializedSubject.specialization.major = :majorManagement
        """;

        Staffs staff = staffsService.getStaff();
        return entityManager.createQuery(jpql, Long.class)
                .setParameter("campusId", staff.getCampus().getCampusId())
                .setParameter("majorManagement", staff.getMajorManagement())
                .getSingleResult();
    }

    /**
     * 2. Top 5 giảng viên dạy nhiều lớp chuyên ngành nhất
     * Trả về: [lecturerId, fullName, classCount]
     */
    @Override
    public List<Object[]> getTop5LecturersBySpecializedClassCount() {
        String jpql = """
        SELECT l.id, CONCAT(l.firstName, ' ', l.lastName), COUNT(lmc)
        FROM MajorLecturers_SpecializedClasses lmc
        JOIN lmc.lecturer l
        JOIN lmc.specializedClass sc
        WHERE sc.creator.campus.campusId = :campusId
          AND sc.specializedSubject.specialization.major = :majorManagement
        GROUP BY l.id, l.firstName, l.lastName
        ORDER BY COUNT(lmc) DESC
        """;

        Staffs staff = staffsService.getStaff();
        return entityManager.createQuery(jpql, Object[].class)
                .setParameter("campusId", staff.getCampus().getCampusId())
                .setParameter("majorManagement", staff.getMajorManagement())
                .setMaxResults(5)
                .getResultList();
    }

    /**
     * 3. Số lớp chuyên ngành CHƯA có giảng viên nào (CẢNH BÁO ĐỎ)
     */
    @Override
    public long countSpecializedClassesWithoutLecturer() {
        String jpql = """
        SELECT COUNT(sc)
        FROM SpecializedClasses sc
        WHERE sc.creator.campus.campusId = :campusId
          AND sc.specializedSubject.specialization.major = :majorManagement
          AND sc.classId NOT IN (
            SELECT lmc.specializedClass.classId FROM MajorLecturers_SpecializedClasses lmc
          )
        """;

        Staffs staff = staffsService.getStaff();
        return entityManager.createQuery(jpql, Long.class)
                .setParameter("campusId", staff.getCampus().getCampusId())
                .setParameter("majorManagement", staff.getMajorManagement())
                .getSingleResult();
    }

    /**
     * 4. Top 5 lớp chuyên ngành có nhiều giảng viên nhất
     */
    @Override
    public List<Object[]> getTop5SpecializedClassesWithMostLecturers() {
        String jpql = """
        SELECT sc.classId, sc.nameClass, COUNT(lmc)
        FROM MajorLecturers_SpecializedClasses lmc
        JOIN lmc.specializedClass sc
        WHERE sc.creator.campus.campusId = :campusId
          AND sc.specializedSubject.specialization.major = :majorManagement
        GROUP BY sc.classId, sc.nameClass
        ORDER BY COUNT(lmc) DESC
        """;

        Staffs staff = staffsService.getStaff();
        return entityManager.createQuery(jpql, Object[].class)
                .setParameter("campusId", staff.getCampus().getCampusId())
                .setParameter("majorManagement", staff.getMajorManagement())
                .setMaxResults(5)
                .getResultList();
    }

    /**
     * 5. Top 5 giảng viên dạy ÍT lớp chuyên ngành nhất → gợi ý phân công thêm
     */
    @Override
    public List<Object[]> getTop5LecturersWithFewestSpecializedClasses() {
        String jpql = """
        SELECT l.id, CONCAT(l.firstName, ' ', l.lastName), COALESCE(COUNT(lmc), 0)
        FROM MajorLecturers l
        LEFT JOIN MajorLecturers_SpecializedClasses lmc ON l.id = lmc.lecturer.id
        WHERE l.campus.campusId = :campusId
          AND l.majorManagement = :majorManagement
        GROUP BY l.id, l.firstName, l.lastName
        ORDER BY COALESCE(COUNT(lmc), 0) ASC, l.firstName
        """;

        Staffs staff = staffsService.getStaff();
        return entityManager.createQuery(jpql, Object[].class)
                .setParameter("campusId", staff.getCampus().getCampusId())
                .setParameter("majorManagement", staff.getMajorManagement())
                .setMaxResults(5)
                .getResultList();
    }

    @Override
    public List<String> getClassNotificationsForLecturer(String lecturerId) {
        String jpql = """
        SELECT CONCAT('You have been added to specialized class: ',
                      c.nameClass, ' (', 
                      COALESCE(c.specializedSubject.subjectName, 'N/A'), 
                      ') on ', lmc.createdAt)
        FROM MajorLecturers_SpecializedClasses lmc
        JOIN lmc.specializedClass c
        WHERE lmc.lecturer.id = :lecturerId
          AND lmc.notificationType = 'NOTIFICATION_004'
        """;

        return entityManager.createQuery(jpql, String.class)
                .setParameter("lecturerId", lecturerId)
                .getResultList();
    }


    @Override
    public List<MajorLecturers_SpecializedClasses> getClassByLecturer(MajorLecturers lecturers) {
        return entityManager.createQuery("from MajorLecturers_SpecializedClasses ms where ms.lecturer=:lecturers",MajorLecturers_SpecializedClasses.class).
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
            lecturerClass.setLecturer(lecturer);
            lecturerClass.setSpecializedClass(classes);
            lecturerClass.setCreatedAt(LocalDateTime.now());
            lecturerClass.setAddedBy(staffsService.getStaff());
            entityManager.persist(lecturerClass);
        }
    }

    @Override
    public List<MajorLecturers> listLecturersInClass(SpecializedClasses classes) {
        return entityManager.createQuery(
                        "SELECT lc.lecturer FROM MajorLecturers_SpecializedClasses lc WHERE lc.specializedClass = :class",
                        MajorLecturers.class)
                .setParameter("class", classes)
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
                                    "    WHERE lc.lecturer = l " +
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