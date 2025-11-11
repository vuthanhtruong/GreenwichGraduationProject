package com.example.demo.timtable.dao;

import com.example.demo.entity.Enums.DaysOfWeek;
import com.example.demo.room.model.Rooms;
import com.example.demo.timtable.model.MajorTimetable;
import com.example.demo.timtable.model.Slots;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional
public class MajorTimetableDAOImpl implements MajorTimetableDAO {
    @Override
    public MajorTimetable getTimetableByClassSlotDayDate(String classId, String slotId, DaysOfWeek dayOfWeek, LocalDate date) {
        return em.createQuery("""
                            SELECT t FROM MajorTimetable t 
                            WHERE t.classEntity.classId = :classId 
                              AND t.slot.slotId = :slotId 
                              AND t.dayOfWeek = :day 
                              AND t.date = :date
                            """, MajorTimetable.class)
                .setParameter("classId", classId)
                .setParameter("slotId", slotId)
                .setParameter("day", dayOfWeek)
                .setParameter("date", date)
                .getResultList()
                .stream().findFirst().orElse(null);
    }

    @PersistenceContext
    private EntityManager em;

    // === HELPER: Tính ngày từ weekNumber + dayOfWeek ===
    private LocalDate getDateFromWeekAndDay(Integer weekNumber, DaysOfWeek dayOfWeek) {
        LocalDate monday = LocalDate.of(LocalDate.now().getYear(), 1, 1)
                .with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, weekNumber)
                .with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        return monday.plusDays(dayOfWeek.ordinal());
    }

    // === HELPER: Lấy tất cả ngày trong tuần ===
    private List<LocalDate> getWeekDates(Integer weekNumber) {
        LocalDate monday = getDateFromWeekAndDay(weekNumber, DaysOfWeek.MONDAY);
        List<LocalDate> dates = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            dates.add(monday.plusDays(i));
        }
        return dates;
    }

    // === 1. LẤY PHÒNG TRỐNG CHO 1 SLOT + NGÀY + TUẦN ===
    @Override
    public List<Rooms> getAvailableRoomsForSlot(String classId, Slots slots, DaysOfWeek daysOfWeek, Integer WeekNumberInYear) {
        LocalDate date = getDateFromWeekAndDay(WeekNumberInYear, daysOfWeek);
        String slotId = slots.getSlotId();

        // 1. Lớp đã có lịch chưa?
        boolean exists = em.createQuery("""
                SELECT COUNT(t) > 0 FROM MajorTimetable t
                WHERE t.classEntity.classId = :classId
                  AND t.slot.slotId = :slotId
                  AND t.dayOfWeek = :day
                  AND t.date = :date
                """, Boolean.class)
                .setParameter("classId", classId)
                .setParameter("slotId", slotId)
                .setParameter("day", daysOfWeek)
                .setParameter("date", date)
                .getSingleResult();

        if (exists) return List.of();

        // 2. Giảng viên trùng?
        String lecturerId = em.createQuery("""
                SELECT lmc.lecturer.id FROM MajorLecturers_MajorClasses lmc
                WHERE lmc.majorClass.classId = :classId
                """, String.class)
                .setParameter("classId", classId)
                .getResultList()
                .stream().findFirst().orElse(null);

        if (lecturerId != null && lecturerHasConflict(lecturerId, slotId, daysOfWeek, date, classId)) {
            return List.of();
        }

        // 3. Sinh viên trùng?
        List<String> studentIds = em.createQuery("""
                SELECT s.id FROM Students s
                JOIN Students_MajorClasses smc ON s.id = smc.student.id
                WHERE smc.majorClass.classId = :classId
                """, String.class)
                .setParameter("classId", classId)
                .getResultList();

        if (studentIds.stream().anyMatch(id -> studentHasConflict(id, slotId, daysOfWeek, date, classId))) {
            return List.of();
        }

        // 4. Phòng trống?
        return getAvailableRooms(slotId, daysOfWeek, date);
    }

    // === 2. MẢNG 7x6: CÓ THỂ XẾP HAY KHÔNG ===
    @Override
    public String[][] SlotOfTheDayThatCanBeSuccessfullyArranged(String classId, Slots slots, DaysOfWeek daysOfWeek, Integer WeekNumberInYear) {
        String[][] result = new String[7][6];
        List<LocalDate> dates = getWeekDates(WeekNumberInYear);
        List<Slots> allSlots = em.createQuery("SELECT s FROM Slots s ORDER BY s.slotId", Slots.class).getResultList();

        for (int dayIdx = 0; dayIdx < 7; dayIdx++) {
            LocalDate date = dates.get(dayIdx);
            DaysOfWeek day = DaysOfWeek.valueOf(date.getDayOfWeek().name());

            for (int slotIdx = 0; slotIdx < 6 && slotIdx < allSlots.size(); slotIdx++) {
                Slots slot = allSlots.get(slotIdx);
                String slotId = slot.getSlotId();

                // Dùng lại logic từ getAvailableRoomsForSlot
                List<Rooms> rooms = getAvailableRoomsForSlot(classId, slot, day, WeekNumberInYear);
                result[dayIdx][slotIdx] = rooms.isEmpty() ? null : rooms.get(0).getRoomId();
            }
        }

        return result;
    }

    // === 3. LƯU TIMETABLE ===
    @Override
    public void SaveMajorTimetable(MajorTimetable timetable) {
        MajorTimetable existing = em.find(MajorTimetable.class, timetable.getTimetableId());
        if (existing != null) {
            em.merge(timetable);
        } else {
            em.persist(timetable);
        }
    }

    // === HELPER: KIỂM TRA TRÙNG GIẢNG VIÊN ===
    private boolean lecturerHasConflict(String lecturerId, String slotId, DaysOfWeek day, LocalDate date, String excludeClassId) {
        String jpql = """
                SELECT COUNT(*) > 0 FROM (
                    SELECT 1 FROM MajorTimetable t
                    JOIN MajorLecturers_MajorClasses lmc ON t.classEntity.classId = lmc.majorClass.classId
                    WHERE lmc.lecturer.id = :lecturerId
                      AND t.slot.slotId = :slotId
                      AND t.dayOfWeek = :day
                      AND t.date = :date
                      AND t.classEntity.classId != :excludeClassId
                    UNION ALL
                    SELECT 1 FROM MinorTimetable t
                    JOIN MinorLecturers_MinorClasses lmc ON t.minorClass.classId = lmc.minorClass.classId
                    WHERE lmc.lecturer.id = :lecturerId
                      AND t.slot.slotId = :slotId
                      AND t.dayOfWeek = :day
                      AND t.date = :date
                    UNION ALL
                    SELECT 1 FROM SpecializedTimetable t
                    JOIN MajorLecturers_SpecializedClasses lmc ON t.specializedClass.classId = lmc.specializedClass.classId
                    WHERE lmc.lecturer.id = :lecturerId
                      AND t.slot.slotId = :slotId
                      AND t.dayOfWeek = :day
                      AND t.date = :date
                ) AS sub
                """;
        return em.createQuery(jpql, Boolean.class)
                .setParameter("lecturerId", lecturerId)
                .setParameter("slotId", slotId)
                .setParameter("day", day)
                .setParameter("date", date)
                .setParameter("excludeClassId", excludeClassId)
                .getSingleResult();
    }

    // === HELPER: KIỂM TRA TRÙNG SINH VIÊN ===
    private boolean studentHasConflict(String studentId, String slotId, DaysOfWeek day, LocalDate date, String excludeClassId) {
        String jpql = """
                SELECT COUNT(*) > 0 FROM (
                    SELECT 1 FROM MajorTimetable t
                    JOIN Students_MajorClasses smc ON t.classEntity.classId = smc.majorClass.classId
                    WHERE smc.student.id = :studentId
                      AND t.slot.slotId = :slotId
                      AND t.dayOfWeek = :day
                      AND t.date = :date
                      AND t.classEntity.classId != :excludeClassId
                    UNION ALL
                    SELECT 1 FROM MinorTimetable t
                    JOIN Students_MinorClasses smc ON t.minorClass.classId = smc.minorClass.classId
                    WHERE smc.student.id = :studentId
                      AND t.slot.slotId = :slotId
                      AND t.dayOfWeek = :day
                      AND t.date = :date
                    UNION ALL
                    SELECT 1 FROM SpecializedTimetable t
                    JOIN Students_SpecializedClasses smc ON t.specializedClass.classId = smc.specializedClass.classId
                    WHERE smc.student.id = :studentId
                      AND t.slot.slotId = :slotId
                      AND t.dayOfWeek = :day
                      AND t.date = :date
                ) AS sub
                """;
        return em.createQuery(jpql, Boolean.class)
                .setParameter("studentId", studentId)
                .setParameter("slotId", slotId)
                .setParameter("day", day)
                .setParameter("date", date)
                .setParameter("excludeClassId", excludeClassId)
                .getSingleResult();
    }

    // === HELPER: PHÒNG TRỐNG ===
    private List<Rooms> getAvailableRooms(String slotId, DaysOfWeek day, LocalDate date) {
        String jpql = """
                SELECT r FROM Rooms r
                WHERE r.roomId NOT IN (
                    SELECT t.room.roomId FROM MajorTimetable t
                    WHERE t.slot.slotId = :slotId AND t.dayOfWeek = :day AND t.date = :date
                    UNION
                    SELECT t.room.roomId FROM MinorTimetable t
                    WHERE t.slot.slotId = :slotId AND t.dayOfWeek = :day AND t.date = :date
                    UNION
                    SELECT t.room.roomId FROM SpecializedTimetable t
                    WHERE t.slot.slotId = :slotId AND t.dayOfWeek = :day AND t.date = :date
                )
                """;
        return em.createQuery(jpql, Rooms.class)
                .setParameter("slotId", slotId)
                .setParameter("day", day)
                .setParameter("date", date)
                .getResultList();
    }
}