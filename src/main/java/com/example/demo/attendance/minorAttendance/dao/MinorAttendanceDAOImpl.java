// src/main/java/com/example/demo/attendance/minorAttendance/dao/MinorAttendanceDAOImpl.java
package com.example.demo.attendance.minorAttendance.dao;

import com.example.demo.attendance.minorAttendance.model.MinorAttendance;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class MinorAttendanceDAOImpl implements MinorAttendanceDAO {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<MinorAttendance> getAttendanceByTimetable(String timetableId) {
        String jpql = """
            SELECT a FROM MinorAttendance a
            JOIN FETCH a.student
            LEFT JOIN FETCH a.markedBy
            WHERE a.timetable.timetableId = :timetableId
            ORDER BY a.student.firstName
            """;
        return em.createQuery(jpql, MinorAttendance.class)
                .setParameter("timetableId", timetableId)
                .getResultList();
    }

    @Override
    public void save(MinorAttendance attendance) {
        if (attendance.getAttendanceId() == null || attendance.getAttendanceId().isBlank()) {
            attendance.setAttendanceId(java.util.UUID.randomUUID().toString());
            attendance.setCreatedAt(java.time.LocalDateTime.now());
            em.persist(attendance);
        } else {
            em.merge(attendance);
        }
    }

    @Override
    public void saveAll(List<MinorAttendance> attendances) {
        for (MinorAttendance a : attendances) {
            save(a);
        }
    }

    @Override
    public MinorAttendance findByTimetableAndStudent(String timetableId, String studentId) {
        String jpql = """
            SELECT a FROM MinorAttendance a
            WHERE a.timetable.timetableId = :timetableId
              AND a.student.id = :studentId
            """;
        return em.createQuery(jpql, MinorAttendance.class)
                .setParameter("timetableId", timetableId)
                .setParameter("studentId", studentId)
                .getResultList()
                .stream()
                .findFirst()
                .orElse(null);
    }
}