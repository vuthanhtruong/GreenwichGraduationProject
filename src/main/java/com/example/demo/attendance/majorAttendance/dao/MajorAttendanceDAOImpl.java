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

    @Override
    public long countAttendanceSessionsThisWeek(String campusId, Integer week, Integer year) {
        String jpql = """
        SELECT COUNT(DISTINCT t.timetableId)
        FROM MajorTimetable t
        JOIN t.classEntity c
        WHERE c.creator.campus.campusId = :campusId
          AND t.weekOfYear = :week
          AND t.year = :year
          AND EXISTS (SELECT 1 FROM MajorAttendance a WHERE a.timetable = t)
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
    SELECT COALESCE( (p.presentCount * 100.0) / p.totalCount, 0.0 )
    FROM (
        SELECT
            SUM(CASE WHEN a.status = 'PRESENT' THEN 1 ELSE 0 END) AS presentCount,
            COUNT(a) AS totalCount
        FROM MajorAttendance a
        JOIN a.timetable t
        JOIN t.classEntity c
        WHERE c.creator.campus.campusId = :campusId
          AND t.weekOfYear = :week
          AND t.year = :year
    ) p
    """;

        Double result = em.createQuery(jpql, Double.class)
                .setParameter("campusId", campusId)
                .setParameter("week", week)
                .setParameter("year", year)
                .getSingleResult();

        return result != null ? result : 0.0;
    }


    @Override
    public List<Object[]> getTop5ClassesLowestAttendanceThisWeek(
            String campusId, Integer week, Integer year) {

        String jpql = """
    SELECT c.classId,
           c.nameClass,
           (SUM(CASE WHEN a.status = 'PRESENT' THEN 1 ELSE 0 END) * 100.0)
           / COUNT(a)
    FROM MajorAttendance a
    JOIN a.timetable t
    JOIN t.classEntity c
    WHERE c.creator.campus.campusId = :campusId
      AND t.weekOfYear = :week
      AND t.year = :year
    GROUP BY c.classId, c.nameClass
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
        FROM MajorAttendance a
        JOIN a.timetable t
        JOIN t.classEntity c
        JOIN a.student s
        WHERE c.creator.campus.campusId = :campusId
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