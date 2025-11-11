package com.example.demo.timtable.specializedTimetable.dao;

import com.example.demo.entity.Enums.DaysOfWeek;
import com.example.demo.room.model.Rooms;
import com.example.demo.timtable.majorTimetable.model.Slots;
import com.example.demo.timtable.specializedTimetable.model.SpecializedTimetable;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@Transactional
public class SpecializedTimetableDAOImpl implements SpecializedTimetableDAO {

    @PersistenceContext
    private EntityManager em;

    // === 1. KIỂM TRA LỊCH ĐÃ TỒN TẠI ===
    @Override
    public SpecializedTimetable getTimetableByClassSlotDayWeek(
            String classId, String campusId, String slotId, DaysOfWeek dayOfWeek, Integer weekOfYear, Integer year) {
        String jpql = """
            SELECT t FROM SpecializedTimetable t
            JOIN t.specializedClass c
            WHERE c.classId = :classId
              AND c.creator.campus.campusId = :campusId
              AND t.slot.slotId = :slotId
              AND t.dayOfWeek = :day
              AND t.weekOfYear = :week
              AND t.year = :year
            """;
        return em.createQuery(jpql, SpecializedTimetable.class)
                .setParameter("classId", classId)
                .setParameter("campusId", campusId)
                .setParameter("slotId", slotId)
                .setParameter("day", dayOfWeek)
                .setParameter("week", weekOfYear)
                .setParameter("year", year)
                .getResultList()
                .stream().findFirst().orElse(null);
    }

    // === 2. LẤY PHÒNG CÒN TRỐNG ===
    @Override
    public List<Rooms> getAvailableRoomsForSlot(
            String classId, String campusId, Slots slots, DaysOfWeek daysOfWeek, Integer weekNumberInYear, Integer year) {

        String slotId = slots.getSlotId();

        // 1. Lớp đã có lịch chưa?
        if (getTimetableByClassSlotDayWeek(classId, campusId, slotId, daysOfWeek, weekNumberInYear, year) != null) {
            return List.of();
        }

        // 2. LẤY GIẢNG VIÊN
        List<String> lecturerIds = em.createQuery("""
                SELECT lmc.lecturer.id FROM MajorLecturers_SpecializedClasses lmc
                JOIN lmc.specializedClass c
                WHERE c.classId = :classId AND c.creator.campus.campusId = :campusId
                """, String.class)
                .setParameter("classId", classId)
                .setParameter("campusId", campusId)
                .getResultList();

        // 3. KIỂM TRA XUNG ĐỘT GIẢNG VIÊN
        if (!lecturerIds.isEmpty() && lecturerIds.stream()
                .anyMatch(id -> lecturerHasConflict(id, slotId, daysOfWeek, weekNumberInYear, year, classId, campusId))) {
            return List.of();
        }

        // 4. LẤY SINH VIÊN
        List<String> studentIds = em.createQuery("""
                SELECT s.id FROM Students s
                JOIN Students_SpecializedClasses ssc ON s.id = ssc.student.id
                JOIN ssc.specializedClass c
                WHERE c.classId = :classId AND c.creator.campus.campusId = :campusId
                """, String.class)
                .setParameter("classId", classId)
                .setParameter("campusId", campusId)
                .getResultList();

        // 5. KIỂM TRA XUNG ĐỘT SINH VIÊN
        if (!studentIds.isEmpty() && studentIds.stream()
                .anyMatch(id -> studentHasConflict(id, slotId, daysOfWeek, weekNumberInYear, year, classId, campusId))) {
            return List.of();
        }

        // 6. TRẢ VỀ PHÒNG TRỐNG TRONG CÙNG CAMPUS
        return getAvailableRoomsInCampus(slotId, daysOfWeek, weekNumberInYear, year, campusId);
    }

    // === 3. MA TRẬN GỢI Ý PHÒNG ===
    @Override
    public String[][] SlotOfTheDayThatCanBeSuccessfullyArranged(
            String classId, String campusId, Slots slots, DaysOfWeek daysOfWeek, Integer weekNumberInYear, Integer year) {
        String[][] result = new String[7][6];
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

    // === 4. LƯU LỊCH ===
    @Override
    public void saveSpecializedTimetable(SpecializedTimetable timetable, String campusId) {
        if (timetable.getTimetableId() == null || timetable.getTimetableId().isBlank()) {
            timetable.setTimetableId(java.util.UUID.randomUUID().toString());
        }
        if (timetable.getCreatedAt() == null) {
            timetable.setCreatedAt(LocalDateTime.now());
        }

        SpecializedTimetable existing = em.find(SpecializedTimetable.class, timetable.getTimetableId());
        if (existing != null) {
            em.merge(timetable);
        } else {
            em.persist(timetable);
        }
    }

    // === 5. LẤY TẤT CẢ LỊCH TRONG TUẦN ===
    @Override
    public List<SpecializedTimetable> getSpecializedTimetablesByWeekInYear(Integer weekInYear, Integer year, String campusId) {
        return em.createQuery("""
            SELECT t FROM SpecializedTimetable t
            JOIN t.specializedClass c
            WHERE t.weekOfYear = :week
              AND t.year = :year
              AND c.creator.campus.campusId = :campusId
            ORDER BY t.dayOfWeek, t.slot.startTime
            """, SpecializedTimetable.class)
                .setParameter("week", weekInYear)
                .setParameter("year", year)
                .setParameter("campusId", campusId)
                .getResultList();
    }

    // === 6. XÓA ===
    @Override
    public void delete(SpecializedTimetable timetable) {
        if (em.contains(timetable)) {
            em.remove(timetable);
        } else {
            em.remove(em.merge(timetable));
        }
    }

    // === 7. LẤY THEO ID ===
    @Override
    public SpecializedTimetable getById(String timetableId) {
        return em.find(SpecializedTimetable.class, timetableId);
    }

    // === 8. ĐẾM SLOT TRONG TUẦN ===
    @Override
    public int countBookedSlotsInWeek(String classId, Integer week, Integer year, String campusId) {
        String jpql = """
            SELECT COUNT(t) FROM SpecializedTimetable t
            JOIN t.specializedClass c
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

    // === 9. ĐẾM TỔNG SLOT ===
    @Override
    public int countTotalBookedSlots(String classId) {
        String jpql = "SELECT COUNT(t) FROM SpecializedTimetable t WHERE t.specializedClass.classId = :classId";
        Long count = em.createQuery(jpql, Long.class)
                .setParameter("classId", classId)
                .getSingleResult();
        return count.intValue();
    }

    // === 10. LẤY TẤT CẢ LỊCH CỦA LỚP ===
    @Override
    public List<SpecializedTimetable> getAllSchedulesByClass(String classId) {
        String jpql = """
            SELECT t FROM SpecializedTimetable t
            JOIN FETCH t.room
            JOIN FETCH t.slot
            LEFT JOIN FETCH t.creator
            WHERE t.specializedClass.classId = :classId
            ORDER BY t.year DESC, t.weekOfYear DESC, t.dayOfWeek, t.slot.startTime
            """;
        return em.createQuery(jpql, SpecializedTimetable.class)
                .setParameter("classId", classId)
                .getResultList();
    }

    // === HELPER: XUNG ĐỘT GIẢNG VIÊN ===
    private boolean lecturerHasConflict(String lecturerId, String slotId, DaysOfWeek day, Integer weekOfYear, Integer year, String excludeClassId, String campusId) {
        String jpql = """
                SELECT COUNT(*) > 0 FROM (
                    SELECT 1 FROM SpecializedTimetable t
                    JOIN t.specializedClass c
                    JOIN MajorLecturers_SpecializedClasses lmc ON c.classId = lmc.specializedClass.classId
                    WHERE lmc.lecturer.id = :lecturerId
                      AND c.creator.campus.campusId = :campusId
                      AND t.slot.slotId = :slotId
                      AND t.dayOfWeek = :day
                      AND t.weekOfYear = :week
                      AND t.year = :year
                      AND c.classId != :excludeClassId
                    UNION ALL
                    SELECT 1 FROM MajorTimetable t
                    JOIN t.classEntity c
                    JOIN MajorLecturers_MajorClasses lmc ON c.classId = lmc.majorClass.classId
                    WHERE lmc.lecturer.id = :lecturerId
                      AND c.creator.campus.campusId = :campusId
                      AND t.slot.slotId = :slotId
                      AND t.dayOfWeek = :day
                      AND t.weekOfYear = :week
                      AND t.year = :year
                    UNION ALL
                    SELECT 1 FROM MinorTimetable t
                    JOIN t.minorClass c
                    JOIN MinorLecturers_MinorClasses lmc ON c.classId = lmc.minorClass.classId
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

    // === HELPER: XUNG ĐỘT SINH VIÊN ===
    private boolean studentHasConflict(String studentId, String slotId, DaysOfWeek day, Integer weekOfYear, Integer year, String excludeClassId, String campusId) {
        String jpql = """
                SELECT COUNT(*) > 0 FROM (
                    SELECT 1 FROM SpecializedTimetable t
                    JOIN t.specializedClass c
                    JOIN Students_SpecializedClasses ssc ON c.classId = ssc.specializedClass.classId
                    WHERE ssc.student.id = :studentId
                      AND c.creator.campus.campusId = :campusId
                      AND t.slot.slotId = :slotId
                      AND t.dayOfWeek = :day
                      AND t.weekOfYear = :week
                      AND t.year = :year
                      AND c.classId != :excludeClassId
                    UNION ALL
                    SELECT 1 FROM MajorTimetable t
                    JOIN t.classEntity c
                    JOIN Students_MajorClasses smc ON c.classId = smc.majorClass.classId
                    WHERE smc.student.id = :studentId
                      AND c.creator.campus.campusId = :campusId
                      AND t.slot.slotId = :slotId
                      AND t.dayOfWeek = :day
                      AND t.weekOfYear = :week
                      AND t.year = :year
                    UNION ALL
                    SELECT 1 FROM MinorTimetable t
                    JOIN t.minorClass c
                    JOIN Students_MinorClasses smc ON c.classId = smc.minorClass.classId
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

    // === HELPER: PHÒNG TRỐNG TRONG CAMPUS ===
    private List<Rooms> getAvailableRoomsInCampus(String slotId, DaysOfWeek day, Integer weekOfYear, Integer year, String campusId) {
        String jpql = """
                SELECT r FROM Rooms r
                WHERE r.campus.campusId = :campusId
                  AND r.roomId NOT IN (
                    SELECT t.room.roomId FROM SpecializedTimetable t
                    JOIN t.specializedClass c
                    WHERE c.creator.campus.campusId = :campusId
                      AND t.slot.slotId = :slotId AND t.dayOfWeek = :day AND t.weekOfYear = :week AND t.year = :year
                    UNION
                    SELECT t.room.roomId FROM MajorTimetable t
                    JOIN t.classEntity c
                    WHERE c.creator.campus.campusId = :campusId
                      AND t.slot.slotId = :slotId AND t.dayOfWeek = :day AND t.weekOfYear = :week AND t.year = :year
                    UNION
                    SELECT t.room.roomId FROM MinorTimetable t
                    JOIN t.minorClass c
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
}