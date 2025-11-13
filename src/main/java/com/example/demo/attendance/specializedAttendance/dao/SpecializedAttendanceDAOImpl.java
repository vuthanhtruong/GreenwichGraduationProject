// src/main/java/com/example/demo/attendance/specializedAttendance/dao/SpecializedAttendanceDAOImpl.java
package com.example.demo.attendance.specializedAttendance.dao;

import com.example.demo.attendance.specializedAttendance.model.SpecializedAttendance;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class SpecializedAttendanceDAOImpl implements SpecializedAttendanceDAO {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<SpecializedAttendance> getAttendanceByTimetable(String timetableId) {
        String jpql = """
            SELECT a FROM SpecializedAttendance a
            JOIN FETCH a.student
            LEFT JOIN FETCH a.markedBy
            WHERE a.timetable.timetableId = :timetableId
            ORDER BY a.student.firstName
            """;
        return em.createQuery(jpql, SpecializedAttendance.class)
                .setParameter("timetableId", timetableId)
                .getResultList();
    }

    @Override
    public void save(SpecializedAttendance attendance) {
        if (attendance.getAttendanceId() == null || attendance.getAttendanceId().isBlank()) {
            attendance.setAttendanceId(java.util.UUID.randomUUID().toString());
            em.persist(attendance);
        } else {
            em.merge(attendance);
        }
    }

    @Override
    public void saveAll(List<SpecializedAttendance> attendances) {
        for (SpecializedAttendance a : attendances) {
            save(a);
        }
    }

    @Override
    public SpecializedAttendance findByTimetableAndStudent(String timetableId, String studentId) {
        String jpql = """
            SELECT a FROM SpecializedAttendance a
            WHERE a.timetable.timetableId = :timetableId
              AND a.student.id = :studentId
            """;
        return em.createQuery(jpql, SpecializedAttendance.class)
                .setParameter("timetableId", timetableId)
                .setParameter("studentId", studentId)
                .getResultList()
                .stream()
                .findFirst()
                .orElse(null);
    }
}