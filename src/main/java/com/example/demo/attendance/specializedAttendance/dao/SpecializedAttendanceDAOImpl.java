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

    @Override
    public long countAttendanceSessionsThisWeek(String campusId, Integer week, Integer year) {
        String jpql = """
        SELECT COUNT(DISTINCT t.timetableId)
        FROM SpecializedTimetable t
        JOIN t.specializedClass sc
        WHERE sc.creator.campus.campusId = :campusId
          AND t.weekOfYear = :week
          AND t.year = :year
          AND EXISTS (SELECT 1 FROM SpecializedAttendance a WHERE a.timetable = t)
        """;
        Long count = em.createQuery(jpql, Long.class)
                .setParameter("campusId", campusId)
                .setParameter("week", week)
                .setParameter("year", year)
                .getSingleResult();
        return count != null ? count : 0L;
    }

    @Override
    public double getAverageAttendanceRateThisWeek(String campusId, Integer week, Integer year) {
        String jpql = """
        SELECT 
            COALESCE(
                CAST(SUM(CASE WHEN a.status = 'PRESENT' THEN 1 ELSE 0 END) AS double) 
                / NULLIF(COUNT(a), 0) * 100, 0.0)
        FROM SpecializedAttendance a
        JOIN a.timetable t
        JOIN t.specializedClass sc
        WHERE sc.creator.campus.campusId = :campusId
          AND t.weekOfYear = :week
          AND t.year = :year
        """;
        Double rate = em.createQuery(jpql, Double.class)
                .setParameter("campusId", campusId)
                .setParameter("week", week)
                .setParameter("year", year)
                .getSingleResult();
        return rate != null ? rate : 0.0;
    }

    @Override
    public List<Object[]> getTop5ClassesLowestAttendanceThisWeek(String campusId, Integer week, Integer year) {
        String jpql = """
        SELECT sc.classId, sc.nameClass,
               COALESCE(
                   CAST(SUM(CASE WHEN a.status = 'PRESENT' THEN 1 ELSE 0 END) AS double) 
                   / NULLIF(COUNT(a), 0) * 100, 0.0)
        FROM SpecializedAttendance a
        JOIN a.timetable t
        JOIN t.specializedClass sc
        WHERE sc.creator.campus.campusId = :campusId
          AND t.weekOfYear = :week
          AND t.year = :year
        GROUP BY sc.classId, sc.nameClass
        HAVING COUNT(a) > 0
        ORDER BY 3 ASC
        """;
        return em.createQuery(jpql, Object[].class)
                .setParameter("campusId", campusId)
                .setParameter("week", week)
                .setParameter("year", year)
                .setMaxResults(5)
                .getResultList();
    }

    @Override
    public long countStudentsWithManyAbsencesThisWeek(String campusId, Integer week, Integer year) {
        String jpql = """
        SELECT COUNT(DISTINCT s.id)
        FROM SpecializedAttendance a
        JOIN a.timetable t
        JOIN t.specializedClass sc
        JOIN a.student s
        WHERE sc.creator.campus.campusId = :campusId
          AND t.weekOfYear = :week
          AND t.year = :year
          AND a.status = 'ABSENT'
        GROUP BY s.id
        HAVING COUNT(a) >= 3
        """;
        List<Long> result = em.createQuery(jpql, Long.class)
                .setParameter("campusId", campusId)
                .setParameter("week", week)
                .setParameter("year", year)
                .getResultList();
        return result != null ? result.size() : 0L;
    }

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