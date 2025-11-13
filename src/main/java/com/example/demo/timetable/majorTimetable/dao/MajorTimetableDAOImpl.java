package com.example.demo.timetable.majorTimetable.dao;

import com.example.demo.entity.Enums.DaysOfWeek;
import com.example.demo.room.model.Rooms;
import com.example.demo.timetable.majorTimetable.model.MajorTimetable;
import com.example.demo.timetable.majorTimetable.model.Slots;
import com.example.demo.timetable.majorTimetable.service.SlotsService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@Transactional
public class MajorTimetableDAOImpl implements MajorTimetableDAO {
    @Override
    public MajorTimetable getMajorTimetableById(String timetableId) {
        return em.find(MajorTimetable.class, timetableId);
    }

    private final SlotsService slotsService;

    public MajorTimetableDAOImpl(SlotsService slotsService) {
        this.slotsService = slotsService;
    }

    @Override
    public List<MajorTimetable> getAllSchedulesByClass(String classId) {
        String jpql = """
        SELECT t FROM MajorTimetable t
        JOIN FETCH t.room
        JOIN FETCH t.slot
        LEFT JOIN FETCH t.creator
        WHERE t.classEntity.classId = :classId
        ORDER BY t.year DESC, t.weekOfYear DESC, t.dayOfWeek, t.slot.startTime
        """;
        return em.createQuery(jpql, MajorTimetable.class)
                .setParameter("classId", classId)
                .getResultList();
    }


    @Override
    public int countTotalBookedSlots(String classId) {
        String jpql = "SELECT COUNT(t) FROM MajorTimetable t WHERE t.classEntity.classId = :classId";
        Long count = em.createQuery(jpql, Long.class)
                .setParameter("classId", classId)
                .getSingleResult();
        return count.intValue();
    }

    @Override
    public int countBookedSlotsInWeek(String classId, Integer week, Integer year, String campusId) {
        String jpql = """
        SELECT COUNT(t) FROM MajorTimetable t
        JOIN t.classEntity c
        WHERE c.classId = :classId
          AND c.creator.campus.campusId = :campusId
          AND t.weekOfYear = :week
          AND t.year = :year
        """;
        Long count = em.createQuery(jpql, Long.class)
                .setParameter("classId", classId)
                .setParameter("campusId", campusId)
                .setParameter("week", week)
                .setParameter("year", year)
                .getSingleResult();
        return count.intValue();
    }

    @Override
    public MajorTimetable getById(String timetableId) {
        return em.find(MajorTimetable.class, timetableId);
    }

    @Override
    public void delete(MajorTimetable timetable) {
        if (em.contains(timetable)) {
            em.remove(timetable);
        } else {
            em.remove(em.merge(timetable));
        }
    }

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<MajorTimetable> getMajorTimetablesByWeekInYear(Integer weekInYear, Integer year, String campusId) {
        return em.createQuery("""
            SELECT t FROM MajorTimetable t
            JOIN t.classEntity c
            WHERE t.weekOfYear = :week
              AND t.year = :year
              AND c.creator.campus.campusId = :campusId
            ORDER BY t.dayOfWeek, t.slot.startTime
            """, MajorTimetable.class)
                .setParameter("week", weekInYear)
                .setParameter("year", year)
                .setParameter("campusId", campusId)
                .getResultList();
    }

    @Override
    public MajorTimetable getTimetableByClassSlotDayWeek(
            String classId, String campusId, String slotId, DaysOfWeek dayOfWeek, Integer weekOfYear, Integer year) {
        return em.createQuery("""
                SELECT t FROM MajorTimetable t 
                JOIN t.classEntity c
                WHERE c.classId = :classId 
                  AND c.creator.campus.campusId = :campusId
                  AND t.slot.slotId = :slotId 
                  AND t.dayOfWeek = :day 
                  AND t.weekOfYear = :week
                  AND t.year = :year
                """, MajorTimetable.class)
                .setParameter("classId", classId)
                .setParameter("campusId", campusId)
                .setParameter("slotId", slotId)
                .setParameter("day", dayOfWeek)
                .setParameter("week", weekOfYear)
                .setParameter("year", year)
                .getResultList()
                .stream().findFirst().orElse(null);
    }

    @Override
    public List<Rooms> getAvailableRoomsForSlot(
            String classId, String campusId, Slots slots, DaysOfWeek daysOfWeek, Integer weekOfYear, Integer year) {
        String slotId = slots.getSlotId();

        // 1. Lớp đã có lịch chưa?
        if (getTimetableByClassSlotDayWeek(classId, campusId, slotId, daysOfWeek, weekOfYear, year) != null) {
            return List.of();
        }

        // 2. LẤY GIẢNG VIÊN
        List<String> lecturerIds = em.createQuery("""
                SELECT lmc.lecturer.id FROM MajorLecturers_MajorClasses lmc
                JOIN lmc.majorClass c
                WHERE c.classId = :classId AND c.creator.campus.campusId = :campusId
                """, String.class)
                .setParameter("classId", classId)
                .setParameter("campusId", campusId)
                .getResultList();

        // 3. KIỂM TRA XUNG ĐỘT GIẢNG VIÊN
        if (!lecturerIds.isEmpty() && lecturerIds.stream()
                .anyMatch(id -> lecturerHasConflict(id, slotId, daysOfWeek, weekOfYear, year, classId, campusId))) {
            return List.of();
        }

        // 4. LẤY SINH VIÊN
        List<String> studentIds = em.createQuery("""
                SELECT s.id FROM Students s
                JOIN Students_MajorClasses smc ON s.id = smc.student.id
                JOIN smc.majorClass c
                WHERE c.classId = :classId AND c.creator.campus.campusId = :campusId
                """, String.class)
                .setParameter("classId", classId)
                .setParameter("campusId", campusId)
                .getResultList();

        // 5. KIỂM TRA XUNG ĐỘT SINH VIÊN
        if (!studentIds.isEmpty() && studentIds.stream()
                .anyMatch(id -> studentHasConflict(id, slotId, daysOfWeek, weekOfYear, year, classId, campusId))) {
            return List.of();
        }

        // 6. TRẢ VỀ PHÒNG TRỐNG TRONG CÙNG CAMPUS
        return getAvailableRoomsInCampus(slotId, daysOfWeek, weekOfYear, year, campusId);
    }

    @Override
    public String[][] SlotOfTheDayThatCanBeSuccessfullyArranged(
            String classId, String campusId, Slots slots, DaysOfWeek daysOfWeek, Integer weekNumberInYear, Integer year) {
        String[][] result = new String[7][slotsService.getSlots().size()];
        List<Slots> allSlots = em.createQuery("SELECT s FROM Slots s ORDER BY s.slotId", Slots.class).getResultList();

        for (int dayIdx = 0; dayIdx < 7; dayIdx++) {
            DaysOfWeek day = DaysOfWeek.values()[dayIdx];
            for (int slotIdx = 0; slotIdx < Math.min(6, allSlots.size()); slotIdx++) {
                Slots slot = allSlots.get(slotIdx);
                List<Rooms> rooms = getAvailableRoomsForSlot(classId, campusId, slot, day, weekNumberInYear, year);
                result[dayIdx][slotIdx] = rooms.isEmpty() ? null : rooms.get(0).getRoomId();
            }
        }
        return result;
    }

    @Override
    public void SaveMajorTimetable(MajorTimetable timetable, String campusId) {
        MajorTimetable existing = em.find(MajorTimetable.class, timetable.getTimetableId());
        if (existing != null) {
            existing.setCreatedAt(LocalDateTime.now());
            em.merge(timetable);
        } else {
            em.persist(timetable);
        }
    }

    private boolean lecturerHasConflict(String lecturerId, String slotId, DaysOfWeek day, Integer weekOfYear, Integer year, String excludeClassId, String campusId) {
        String jpql = """
            SELECT COUNT(*) > 0 FROM (
                SELECT 1 AS dummy FROM MajorTimetable t
                JOIN t.classEntity c
                JOIN MajorLecturers_MajorClasses lmc ON c.classId = lmc.majorClass.classId
                WHERE lmc.lecturer.id = :lecturerId
                  AND c.creator.campus.campusId = :campusId
                  AND t.slot.slotId = :slotId
                  AND t.dayOfWeek = :day
                  AND t.weekOfYear = :week
                  AND t.year = :year
                  AND c.classId != :excludeClassId
                UNION ALL
                SELECT 1 AS dummy FROM MinorTimetable t
                JOIN t.minorClass c
                JOIN MinorLecturers_MinorClasses lmc ON c.classId = lmc.minorClass.classId
                WHERE lmc.lecturer.id = :lecturerId
                  AND c.creator.campus.campusId = :campusId
                  AND t.slot.slotId = :slotId
                  AND t.dayOfWeek = :day
                  AND t.weekOfYear = :week
                  AND t.year = :year
                UNION ALL
                SELECT 1 AS dummy FROM SpecializedTimetable t
                JOIN t.specializedClass c
                JOIN MajorLecturers_SpecializedClasses lmc ON c.classId = lmc.specializedClass.classId
                WHERE lmc.lecturer.id = :lecturerId
                  AND c.creator.campus.campusId = :campusId
                  AND t.slot.slotId = :slotId
                  AND t.dayOfWeek = :day
                  AND t.weekOfYear = :week
                  AND t.year = :year
            ) AS sub
            """;
        return em.createQuery(jpql, Boolean.class)
                .setParameter("lecturerId", lecturerId)
                .setParameter("slotId", slotId)
                .setParameter("day", day)
                .setParameter("week", weekOfYear)
                .setParameter("year", year)
                .setParameter("excludeClassId", excludeClassId)
                .setParameter("campusId", campusId)
                .getSingleResult();
    }

    private boolean studentHasConflict(String studentId, String slotId, DaysOfWeek day, Integer weekOfYear, Integer year, String excludeClassId, String campusId) {
        String jpql = """
            SELECT COUNT(*) > 0 FROM (
                SELECT 1 AS dummy FROM MajorTimetable t
                JOIN t.classEntity c
                JOIN Students_MajorClasses smc ON c.classId = smc.majorClass.classId
                WHERE smc.student.id = :studentId
                  AND c.creator.campus.campusId = :campusId
                  AND t.slot.slotId = :slotId
                  AND t.dayOfWeek = :day
                  AND t.weekOfYear = :week
                  AND t.year = :year
                  AND c.classId != :excludeClassId
                UNION ALL
                SELECT 1 AS dummy FROM MinorTimetable t
                JOIN t.minorClass c
                JOIN Students_MinorClasses smc ON c.classId = smc.minorClass.classId
                WHERE smc.student.id = :studentId
                  AND c.creator.campus.campusId = :campusId
                  AND t.slot.slotId = :slotId
                  AND t.dayOfWeek = :day
                  AND t.weekOfYear = :week
                  AND t.year = :year
                UNION ALL
                SELECT 1 AS dummy FROM SpecializedTimetable t
                JOIN t.specializedClass c
                JOIN Students_SpecializedClasses smc ON c.classId = smc.specializedClass.classId
                WHERE smc.student.id = :studentId
                  AND c.creator.campus.campusId = :campusId
                  AND t.slot.slotId = :slotId
                  AND t.dayOfWeek = :day
                  AND t.weekOfYear = :week
                  AND t.year = :year
            ) AS sub
            """;
        return em.createQuery(jpql, Boolean.class)
                .setParameter("studentId", studentId)
                .setParameter("slotId", slotId)
                .setParameter("day", day)
                .setParameter("week", weekOfYear)
                .setParameter("year", year)
                .setParameter("excludeClassId", excludeClassId)
                .setParameter("campusId", campusId)
                .getSingleResult();
    }

    // === HELPER: PHÒNG TRỐNG TRONG CÙNG CAMPUS ===
    private List<Rooms> getAvailableRoomsInCampus(String slotId, DaysOfWeek day, Integer weekOfYear, Integer year, String campusId) {
        String jpql = """
                SELECT r FROM Rooms r
                WHERE r.campus.campusId = :campusId
                  AND r.roomId NOT IN (
                    SELECT t.room.roomId FROM MajorTimetable t
                    JOIN t.classEntity c
                    WHERE c.creator.campus.campusId = :campusId
                      AND t.slot.slotId = :slotId AND t.dayOfWeek = :day AND t.weekOfYear = :week AND t.year = :year
                    UNION
                    SELECT t.room.roomId FROM MinorTimetable t
                    JOIN t.minorClass c
                    WHERE c.creator.campus.campusId = :campusId
                      AND t.slot.slotId = :slotId AND t.dayOfWeek = :day AND t.weekOfYear = :week AND t.year = :year
                    UNION
                    SELECT t.room.roomId FROM SpecializedTimetable t
                    JOIN t.specializedClass c
                    WHERE c.creator.campus.campusId = :campusId
                      AND t.slot.slotId = :slotId AND t.dayOfWeek = :day AND t.weekOfYear = :week AND t.year = :year
                )
                """;
        return em.createQuery(jpql, Rooms.class)
                .setParameter("campusId", campusId)
                .setParameter("slotId", slotId)
                .setParameter("day", day)
                .setParameter("week", weekOfYear)
                .setParameter("year", year)
                .getResultList();
    }

    @Override
    public List<MajorTimetable> getMajorTimetableByStudent(String studentId, Integer week, Integer year) {
        String jpql = """
        SELECT t FROM MajorTimetable t
        WHERE t.weekOfYear = :week
          AND t.year = :year
          AND t.classEntity.classId IN (
            SELECT smc.majorClass.classId
            FROM Students_MajorClasses smc 
            WHERE smc.student.id = :studentId
          )
        ORDER BY t.dayOfWeek, t.slot.startTime
        """;

        return em.createQuery(jpql, MajorTimetable.class)
                .setParameter("studentId", studentId)
                .setParameter("week", week)
                .setParameter("year", year)
                .getResultList();
    }
    @Override
    public List<MajorTimetable> getMajorTimetablesByLecturer(String lecturerId, Integer week, Integer year) {
        String jpql = """
        SELECT DISTINCT t FROM MajorTimetable t
        JOIN FETCH t.room
        JOIN FETCH t.slot
        LEFT JOIN FETCH t.creator
        JOIN t.classEntity c
        JOIN MajorLecturers_MajorClasses lmc ON c.classId = lmc.majorClass.classId
        JOIN c.creator staff
        WHERE lmc.lecturer.id = :lecturerId
          AND t.weekOfYear = :week
          AND t.year = :year
        ORDER BY t.dayOfWeek, t.slot.startTime
        """;

        return em.createQuery(jpql, MajorTimetable.class)
                .setParameter("lecturerId", lecturerId)
                .setParameter("week", week)
                .setParameter("year", year)
                .getResultList();
    }

}