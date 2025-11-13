// src/main/java/com/example/demo/attendance/majorAttendance/dao/MajorAttendanceDAOImpl.java
package com.example.demo.attendance.majorAttendance.dao;

import com.example.demo.attendance.majorAttendance.model.MajorAttendance;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class MajorAttendanceDAOImpl implements MajorAttendanceDAO {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<MajorAttendance> getAttendanceByTimetable(String timetableId) {
        String jpql = """
            SELECT a FROM MajorAttendance a
            JOIN FETCH a.student
            LEFT JOIN FETCH a.markedBy
            WHERE a.timetable.timetableId = :timetableId
            ORDER BY a.student.firstName
            """;
        return em.createQuery(jpql, MajorAttendance.class)
                .setParameter("timetableId", timetableId)
                .getResultList();
    }

    @Override
    public void save(MajorAttendance attendance) {
        if (attendance.getAttendanceId() == null) {
            attendance.setAttendanceId(java.util.UUID.randomUUID().toString());
            em.persist(attendance);
        } else {
            em.merge(attendance);
        }
    }

    @Override
    public void saveAll(List<MajorAttendance> attendances) {
        for (MajorAttendance a : attendances) {
            save(a);
        }
    }

    @Override
    public MajorAttendance findByTimetableAndStudent(String timetableId, String studentId) {
        String jpql = """
            SELECT a FROM MajorAttendance a
            WHERE a.timetable.timetableId = :timetableId
              AND a.student.id = :studentId
            """;
        return em.createQuery(jpql, MajorAttendance.class)
                .setParameter("timetableId", timetableId)
                .setParameter("studentId", studentId)
                .getResultList()
                .stream().findFirst().orElse(null);
    }
}