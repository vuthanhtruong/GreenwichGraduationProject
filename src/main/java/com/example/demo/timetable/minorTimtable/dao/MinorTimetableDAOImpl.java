// src/main/java/com/example/demo/timtable/minorTimetable/dao/MinorTimetableDAOImpl.java
package com.example.demo.timetable.minorTimtable.dao;

import com.example.demo.classes.minorClasses.model.MinorClasses;
import com.example.demo.classes.minorClasses.service.MinorClassesService;
import com.example.demo.email_service.dto.ScheduleEmailContext;
import com.example.demo.email_service.service.EmailServiceForStudentService;
import com.example.demo.entity.Enums.DaysOfWeek;
import com.example.demo.room.model.Rooms;
import com.example.demo.room.service.RoomsService;
import com.example.demo.students_Classes.students_MinorClasses.service.StudentsMinorClassesService;
import com.example.demo.timetable.majorTimetable.model.Slots;
import com.example.demo.timetable.majorTimetable.service.SlotsService;
import com.example.demo.timetable.minorTimtable.model.MinorTimetable;
import com.example.demo.user.student.model.Students;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.IsoFields;
import java.util.List;

@Repository
@Transactional
public class MinorTimetableDAOImpl implements MinorTimetableDAO {

    @PersistenceContext
    private EntityManager em;


    private final SlotsService slotsService;

    public MinorTimetableDAOImpl(MinorClassesService minorClassesService, StudentsMinorClassesService studentsMinorClassesService, SlotsService slotsService, EmailServiceForStudentService emailServiceForStudentService) {
        this.minorClassesService = minorClassesService;
        this.studentsMinorClassesService = studentsMinorClassesService;
        this.slotsService = slotsService;
        this.emailServiceForStudentService = emailServiceForStudentService;
    }

    private final MinorClassesService minorClassesService;
    private final StudentsMinorClassesService studentsMinorClassesService;
    private final EmailServiceForStudentService emailServiceForStudentService;

    @Override
    @Transactional
    public void sendScheduleNotification(String classId) {
        try {
            // 1. Lấy lớp phụ (Minor Class)
            MinorClasses minorClass = minorClassesService.getClassById(classId);
            if (minorClass == null) {
                System.err.println("ERROR: Minor class not found with ID: " + classId);
                return;
            }

            // 2. Lấy danh sách sinh viên trong lớp phụ
            List<Students> studentsList = studentsMinorClassesService.getStudentsByClass(minorClass);
            if (studentsList == null || studentsList.isEmpty()) {
                System.out.println("WARN: No students enrolled in minor class: " + classId + " (" + minorClass.getNameClass() + ")");
                return;
            }

            // 3. Gửi email thông báo lịch học cho từng sinh viên
            for (Students student : studentsList) {
                if (student.getEmail() == null || student.getEmail().trim().isEmpty()) {
                    System.out.println("SKIP: Student " + student.getFullName() + " (ID: " + student.getId() + ") has no email.");
                    continue;
                }

                try {
                    ScheduleEmailContext context = new ScheduleEmailContext(
                            student.getId(),
                            student.getFullName(),
                            student.getEmail(),
                            minorClass.getNameClass(),                          // Tên lớp phụ
                            minorClass.getMinorSubject().getSubjectName()            // Tên môn học phụ
                    );

                    String subject = "Minor Class Schedule – " + minorClass.getNameClass();

                    emailServiceForStudentService.sendScheduleNotificationEmail(
                            student.getEmail(),
                            subject,
                            context
                    );

                    System.out.println("SUCCESS: Sent minor class schedule notification to " + student.getEmail() + " (" + student.getFullName() + ")");

                } catch (Exception e) {
                    System.err.println("ERROR: Failed to send email to " + student.getEmail() + " for minor class " + classId + ": " + e.getMessage());
                }
            }

            System.out.println("INFO: Successfully sent schedule notifications for minor class: "
                    + classId + " (" + minorClass.getNameClass() + ") – " + studentsList.size() + " students notified.");

        } catch (Exception e) {
            System.err.println("FATAL ERROR: Failed to send schedule notifications for minor class ID: " + classId);
            e.printStackTrace();
        }
    }

    // Trong MinorTimetableDAOImpl.java (thêm vào cuối class)
    @Override
    public Object[] getDashboardSummaryMinor(String campusId, Integer weekOfYear, Integer year) {
        if (weekOfYear == null || year == null) {
            LocalDate today = LocalDate.now();
            weekOfYear = today.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
            year = today.getYear();
        }

        Long totalClasses = em.createQuery("""
        SELECT COUNT(DISTINCT c.classId) FROM MinorTimetable t
        JOIN t.minorClass c
        WHERE c.creator.campus.campusId = :campusId
          AND t.weekOfYear = :week AND t.year = :year
        """, Long.class)
                .setParameter("campusId", campusId)
                .setParameter("week", weekOfYear)
                .setParameter("year", year)
                .getSingleResult();

        Long totalSlots = em.createQuery("""
        SELECT COUNT(t) FROM MinorTimetable t
        JOIN t.minorClass c
        WHERE c.creator.campus.campusId = :campusId
          AND t.weekOfYear = :week AND t.year = :year
        """, Long.class)
                .setParameter("campusId", campusId)
                .setParameter("week", weekOfYear)
                .setParameter("year", year)
                .getSingleResult();

        Long bookedRooms = em.createQuery("""
        SELECT COUNT(DISTINCT t.room.roomId) FROM MinorTimetable t
        JOIN t.minorClass c
        WHERE c.creator.campus.campusId = :campusId
          AND t.weekOfYear = :week AND t.year = :year
        """, Long.class)
                .setParameter("campusId", campusId)
                .setParameter("week", weekOfYear)
                .setParameter("year", year)
                .getSingleResult();

        Long totalRooms = em.createQuery("SELECT COUNT(r) FROM Rooms r WHERE r.campus.campusId = :campusId", Long.class)
                .setParameter("campusId", campusId)
                .getSingleResult();

        int roomRate = totalRooms == 0 ? 0 : (int) (bookedRooms * 100 / totalRooms);

        return new Object[] { totalClasses.intValue(), totalSlots.intValue(), roomRate, weekOfYear, year };
    }

    @Override
    public List<Object[]> getTop5BusyLecturersMinor(String campusId, Integer weekOfYear, Integer year) {
        String jpql = """
        SELECT l.lecturer.id, l.lecturer.firstName, COUNT(t)
        FROM MinorTimetable t
        JOIN t.minorClass c
        JOIN MinorLecturers_MinorClasses l ON c.classId = l.minorClass.classId
        WHERE c.creator.campus.campusId = :campusId
          AND t.weekOfYear = :week AND t.year = :year
        GROUP BY l.lecturer.id, l.lecturer.firstName
        ORDER BY COUNT(t) DESC
        """;

        return em.createQuery(jpql, Object[].class)
                .setParameter("campusId", campusId)
                .setParameter("week", weekOfYear)
                .setParameter("year", year)
                .setMaxResults(5)
                .getResultList();
    }

    @Override
    public long[] getSlotsPerDayOfWeekMinor(String campusId, Integer weekOfYear, Integer year) {
        long[] result = new long[7];

        String jpql = """
        SELECT t.dayOfWeek, COUNT(t)
        FROM MinorTimetable t
        JOIN t.minorClass c
        WHERE c.creator.campus.campusId = :campusId
          AND t.weekOfYear = :week AND t.year = :year
        GROUP BY t.dayOfWeek
        ORDER BY t.dayOfWeek
        """;

        List<Object[]> list = em.createQuery(jpql, Object[].class)
                .setParameter("campusId", campusId)
                .setParameter("week", weekOfYear)
                .setParameter("year", year)
                .getResultList();

        for (Object[] row : list) {
            DaysOfWeek day = (DaysOfWeek) row[0];
            Long count = (Long) row[1];
            result[day.ordinal()] = count;
        }
        return result;
    }

    @Override
    public List<Object[]> getTop5UsedRoomsMinor(String campusId, Integer weekOfYear, Integer year) {
        String jpql = """
        SELECT t.room.roomId, t.room.roomName, COUNT(t)
        FROM MinorTimetable t
        JOIN t.minorClass c
        WHERE c.creator.campus.campusId = :campusId
          AND t.weekOfYear = :week AND t.year = :year
        GROUP BY t.room.roomId, t.room.roomName
        ORDER BY COUNT(t) DESC
        """;

        return em.createQuery(jpql, Object[].class)
                .setParameter("campusId", campusId)
                .setParameter("week", weekOfYear)
                .setParameter("year", year)
                .setMaxResults(5)
                .getResultList();
    }

    @Override
    public long getUnscheduledMinorClassesCount(String campusId, Integer weekOfYear, Integer year) {
        String jpql = """
        SELECT COUNT(DISTINCT c.classId)
        FROM MinorClasses c
        WHERE c.creator.campus.campusId = :campusId
          AND c.classId NOT IN (
            SELECT t.minorClass.classId FROM MinorTimetable t
            WHERE t.weekOfYear = :week AND t.year = :year
          )
        """;

        Long count = em.createQuery(jpql, Long.class)
                .setParameter("campusId", campusId)
                .setParameter("week", weekOfYear)
                .setParameter("year", year)
                .getSingleResult();

        return count != null ? count : 0L;
    }

    // Thêm vào MinorTimetableDAO interface + impl
    @Override
    public List<MinorTimetable> getMinorTimetableTodayByLecturer(String lecturerId) {
        LocalDate today = LocalDate.now(); // 19/11/2025
        int week = today.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR); // tuần 47 năm 2025
        int year = today.getYear();
        DaysOfWeek dayOfWeek = DaysOfWeek.valueOf(today.getDayOfWeek().name());

        String jpql = """
        SELECT DISTINCT t FROM MinorTimetable t
        JOIN FETCH t.room
        JOIN FETCH t.slot
        LEFT JOIN FETCH t.creator
        JOIN t.minorClass c
        JOIN MinorLecturers_MinorClasses lmc ON c.classId = lmc.minorClass.classId
        WHERE lmc.lecturer.id = :lecturerId
          AND t.weekOfYear = :week
          AND t.year = :year
          AND t.dayOfWeek = :dayOfWeek
        ORDER BY t.slot.startTime
        """;

        return em.createQuery(jpql, MinorTimetable.class)
                .setParameter("lecturerId", lecturerId)
                .setParameter("week", week)
                .setParameter("year", year)
                .setParameter("dayOfWeek", dayOfWeek)
                .getResultList();
    }

    @Override
    public List<MinorTimetable> getAllMinorTimetablesInWeek(Integer weekOfYear, Integer year, String campusId) {
        if (weekOfYear == null || year == null) {
            return List.of();
        }

        StringBuilder jpql = new StringBuilder("""
        SELECT DISTINCT t FROM MinorTimetable t
        JOIN FETCH t.room
        JOIN FETCH t.slot
        LEFT JOIN FETCH t.creator
        JOIN FETCH t.minorClass c
        WHERE t.weekOfYear = :week
          AND t.year = :year
        """);

        if (campusId != null && !campusId.isBlank()) {
            jpql.append(" AND c.creator.campus.campusId = :campusId");
        }

        jpql.append(" ORDER BY t.dayOfWeek, t.slot.startTime");

        var query = em.createQuery(jpql.toString(), MinorTimetable.class)
                .setParameter("week", weekOfYear)
                .setParameter("year", year);

        if (campusId != null && !campusId.isBlank()) {
            query.setParameter("campusId", campusId);
        }

        return query.getResultList();
    }

    @Override
    public List<MinorTimetable> getMinorTimetableByStudentAndClassId(String studentId, String classId) {
        String jpql = """
        SELECT t FROM MinorTimetable t
        JOIN FETCH t.room
        JOIN FETCH t.slot
        LEFT JOIN FETCH t.creator
        JOIN t.minorClass c
        JOIN Students_MinorClasses smc ON c.classId = smc.minorClass.classId
        WHERE smc.student.id = :studentId
          AND c.classId = :classId
        ORDER BY t.year DESC, t.weekOfYear DESC, t.dayOfWeek, t.slot.startTime
        """;

        return em.createQuery(jpql, MinorTimetable.class)
                .setParameter("studentId", studentId)
                .setParameter("classId", classId)
                .getResultList();
    }

    @Override
    public List<MinorClasses> getMinorClassesByMinorTimetable(Integer week, Integer year, String campusId) {
        if (week == null || year == null || campusId == null || campusId.trim().isEmpty()) {
            return List.of();
        }

        String jpql = """
        SELECT DISTINCT c FROM MinorTimetable t
        JOIN t.minorClass c
        WHERE t.weekOfYear = :week
          AND t.year = :year
          AND c.creator.campus.campusId = :campusId
        ORDER BY c.classId
        """;

        return em.createQuery(jpql, MinorClasses.class)
                .setParameter("week", week)
                .setParameter("year", year)
                .setParameter("campusId", campusId)
                .getResultList();
    }

    @Override
    public MinorTimetable getMinorTimetableById(String timetableId) {
        return em.find(MinorTimetable.class, timetableId);
    }

    @Override
    public List<MinorTimetable> getMinorTimetablesByMinorLecturer(
            String lecturerId, Integer week, Integer year) {

        String jpql = """
            SELECT DISTINCT t FROM MinorTimetable t
            JOIN FETCH t.room
            JOIN FETCH t.slot
            LEFT JOIN FETCH t.creator
            JOIN t.minorClass c
            JOIN MinorLecturers_MinorClasses lmc ON c.classId = lmc.minorClass.classId
            JOIN c.creator staff
            WHERE lmc.lecturer.id = :lecturerId
              AND t.weekOfYear = :week
              AND t.year = :year
            ORDER BY t.dayOfWeek, t.slot.startTime
            """;

        return em.createQuery(jpql, MinorTimetable.class)
                .setParameter("lecturerId", lecturerId)
                .setParameter("week", week)
                .setParameter("year", year)
                .getResultList();
    }

    @Override
    public List<MinorTimetable> getAllSchedulesByClass(String classId) {
        String jpql = """
            SELECT t FROM MinorTimetable t
            JOIN FETCH t.room
            JOIN FETCH t.slot
            LEFT JOIN FETCH t.creator
            WHERE t.minorClass.classId = :classId
            ORDER BY t.year DESC, t.weekOfYear DESC, t.dayOfWeek, t.slot.startTime
            """;
        return em.createQuery(jpql, MinorTimetable.class)
                .setParameter("classId", classId)
                .getResultList();
    }

    @Override
    public int countTotalBookedSlots(String classId) {
        String jpql = "SELECT COUNT(t) FROM MinorTimetable t WHERE t.minorClass.classId = :classId";
        Long count = em.createQuery(jpql, Long.class)
                .setParameter("classId", classId)
                .getSingleResult();
        return count.intValue();
    }

    @Override
    public int countBookedSlotsInWeek(String classId, Integer week, Integer year, String campusId) {
        String jpql = """
            SELECT COUNT(t) FROM MinorTimetable t
            JOIN t.minorClass c
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
    public MinorTimetable getById(String timetableId) {
        return em.find(MinorTimetable.class, timetableId);
    }

    @Override
    public void delete(MinorTimetable timetable) {
        if (timetable != null) {
            if (em.contains(timetable)) {
                em.remove(timetable);
            } else {
                em.remove(em.merge(timetable));
            }
        }
    }

    @Override
    public List<MinorTimetable> getMinorTimetablesByWeekInYear(Integer week, Integer year, String campusId) {
        String jpql = """
            SELECT t FROM MinorTimetable t
            JOIN t.minorClass c
            WHERE t.weekOfYear = :week
              AND t.year = :year
              AND c.creator.campus.campusId = :campusId
            ORDER BY t.dayOfWeek, t.slot.startTime
            """;
        return em.createQuery(jpql, MinorTimetable.class)
                .setParameter("week", week)
                .setParameter("year", year)
                .setParameter("campusId", campusId)
                .getResultList();
    }

    @Override
    public MinorTimetable getTimetableByClassSlotDayWeek(
            String classId, String campusId, String slotId, DaysOfWeek dayOfWeek, Integer weekOfYear, Integer year) {
        String jpql = """
            SELECT t FROM MinorTimetable t
            JOIN t.minorClass c
            WHERE c.classId = :classId
              AND c.creator.campus.campusId = :campusId
              AND t.slot.slotId = :slotId
              AND t.dayOfWeek = :day
              AND t.weekOfYear = :week
              AND t.year = :year
            """;
        return em.createQuery(jpql, MinorTimetable.class)
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
            String classId, String campusId, Slots slot, DaysOfWeek day, Integer week, Integer year) {
        String slotId = slot.getSlotId();

        // 1. Lớp đã có lịch chưa?
        if (getTimetableByClassSlotDayWeek(classId, campusId, slotId, day, week, year) != null) {
            return List.of();
        }

        // 2. LẤY GIẢNG VIÊN
        List<String> lecturerIds = em.createQuery("""
            SELECT lmc.lecturer.id FROM MinorLecturers_MinorClasses lmc
            JOIN lmc.minorClass c
            WHERE c.classId = :classId AND c.creator.campus.campusId = :campusId
            """, String.class)
                .setParameter("classId", classId)
                .setParameter("campusId", campusId)
                .getResultList();

        // 3. KIỂM TRA XUNG ĐỘT GIẢNG VIÊN
        if (!lecturerIds.isEmpty() && lecturerIds.stream()
                .anyMatch(id -> lecturerHasConflict(id, slotId, day, week, year, classId, campusId))) {
            return List.of();
        }

        // 4. LẤY SINH VIÊN
        List<String> studentIds = em.createQuery("""
            SELECT s.id FROM Students s
            JOIN Students_MinorClasses smc ON s.id = smc.student.id
            JOIN smc.minorClass c
            WHERE c.classId = :classId AND c.creator.campus.campusId = :campusId
            """, String.class)
                .setParameter("classId", classId)
                .setParameter("campusId", campusId)
                .getResultList();

        // 5. KIỂM TRA XUNG ĐỘT SINH VIÊN
        if (!studentIds.isEmpty() && studentIds.stream()
                .anyMatch(id -> studentHasConflict(id, slotId, day, week, year, classId, campusId))) {
            return List.of();
        }

        // 6. TRẢ VỀ PHÒNG TRỐNG
        return getAvailableRoomsInCampus(slotId, day, week, year, campusId);
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
    public void saveMinorTimetable(MinorTimetable timetable, String campusId) {
        if (timetable.getTimetableId() == null || em.find(MinorTimetable.class, timetable.getTimetableId()) == null) {
            timetable.setTimetableId(java.util.UUID.randomUUID().toString());
            timetable.setCreatedAt(LocalDateTime.now());
            em.persist(timetable);
        } else {
            timetable.setCreatedAt(LocalDateTime.now());
            em.merge(timetable);
        }
    }

    @Override
    public List<MinorTimetable> getMinorTimetableByStudent(String studentId, Integer week, Integer year) {
        String jpql = """
            SELECT t FROM MinorTimetable t
            WHERE t.weekOfYear = :week
              AND t.year = :year
              AND t.minorClass.classId IN (
                SELECT smc.minorClass.classId
                FROM Students_MinorClasses smc
                WHERE smc.student.id = :studentId
              )
            ORDER BY t.dayOfWeek, t.slot.startTime
            """;
        return em.createQuery(jpql, MinorTimetable.class)
                .setParameter("studentId", studentId)
                .setParameter("week", week)
                .setParameter("year", year)
                .getResultList();
    }

    // === XUNG ĐỘT GIẢNG VIÊN ===
    // === XUNG ĐỘT GIẢNG VIÊN ===
    private boolean lecturerHasConflict(String lecturerId, String slotId, DaysOfWeek day, Integer week, Integer year,
                                        String excludeClassId, String campusId) {
        // Kiểm tra xung đột trong MajorTimetable
        String jpql1 = """
        SELECT COUNT(t) FROM MajorTimetable t
        JOIN t.classEntity c
        JOIN MajorLecturers_MajorClasses lmc ON c.classId = lmc.majorClass.classId
        WHERE lmc.lecturer.id = :lecturerId
          AND c.creator.campus.campusId = :campusId
          AND t.slot.slotId = :slotId AND t.dayOfWeek = :day
          AND t.weekOfYear = :week AND t.year = :year
          AND c.classId != :excludeClassId
        """;
        Long count1 = em.createQuery(jpql1, Long.class)
                .setParameter("lecturerId", lecturerId)
                .setParameter("campusId", campusId)
                .setParameter("slotId", slotId)
                .setParameter("day", day)
                .setParameter("week", week)
                .setParameter("year", year)
                .setParameter("excludeClassId", excludeClassId)
                .getSingleResult();

        if (count1 > 0) return true;

        // Kiểm tra xung đột trong MinorTimetable
        String jpql2 = """
        SELECT COUNT(t) FROM MinorTimetable t
        JOIN t.minorClass c
        JOIN MinorLecturers_MinorClasses lmc ON c.classId = lmc.minorClass.classId
        WHERE lmc.lecturer.id = :lecturerId
          AND c.creator.campus.campusId = :campusId
          AND t.slot.slotId = :slotId AND t.dayOfWeek = :day
          AND t.weekOfYear = :week AND t.year = :year
          AND c.classId != :excludeClassId
        """;
        Long count2 = em.createQuery(jpql2, Long.class)
                .setParameter("lecturerId", lecturerId)
                .setParameter("campusId", campusId)
                .setParameter("slotId", slotId)
                .setParameter("day", day)
                .setParameter("week", week)
                .setParameter("year", year)
                .setParameter("excludeClassId", excludeClassId)
                .getSingleResult();

        if (count2 > 0) return true;

        // Kiểm tra xung đột trong SpecializedTimetable
        String jpql3 = """
        SELECT COUNT(t) FROM SpecializedTimetable t
        JOIN t.specializedClass c
        JOIN MajorLecturers_SpecializedClasses lmc ON c.classId = lmc.specializedClass.classId
        WHERE lmc.lecturer.id = :lecturerId
          AND c.creator.campus.campusId = :campusId
          AND t.slot.slotId = :slotId AND t.dayOfWeek = :day
          AND t.weekOfYear = :week AND t.year = :year
        """;
        Long count3 = em.createQuery(jpql3, Long.class)
                .setParameter("lecturerId", lecturerId)
                .setParameter("campusId", campusId)
                .setParameter("slotId", slotId)
                .setParameter("day", day)
                .setParameter("week", week)
                .setParameter("year", year)
                .getSingleResult();

        return count3 > 0;
    }

    // === XUNG ĐỘT SINH VIÊN ===
    private boolean studentHasConflict(String studentId, String slotId, DaysOfWeek day, Integer week, Integer year,
                                       String excludeClassId, String campusId) {
        // Kiểm tra xung đột trong MajorTimetable
        String jpql1 = """
        SELECT COUNT(t) FROM MajorTimetable t
        JOIN t.classEntity c
        JOIN Students_MajorClasses smc ON c.classId = smc.majorClass.classId
        WHERE smc.student.id = :studentId
          AND c.creator.campus.campusId = :campusId
          AND t.slot.slotId = :slotId AND t.dayOfWeek = :day
          AND t.weekOfYear = :week AND t.year = :year
          AND c.classId != :excludeClassId
        """;
        Long count1 = em.createQuery(jpql1, Long.class)
                .setParameter("studentId", studentId)
                .setParameter("campusId", campusId)
                .setParameter("slotId", slotId)
                .setParameter("day", day)
                .setParameter("week", week)
                .setParameter("year", year)
                .setParameter("excludeClassId", excludeClassId)
                .getSingleResult();

        if (count1 > 0) return true;

        // Kiểm tra xung đột trong MinorTimetable
        String jpql2 = """
        SELECT COUNT(t) FROM MinorTimetable t
        JOIN t.minorClass c
        JOIN Students_MinorClasses smc ON c.classId = smc.minorClass.classId
        WHERE smc.student.id = :studentId
          AND c.creator.campus.campusId = :campusId
          AND t.slot.slotId = :slotId AND t.dayOfWeek = :day
          AND t.weekOfYear = :week AND t.year = :year
          AND c.classId != :excludeClassId
        """;
        Long count2 = em.createQuery(jpql2, Long.class)
                .setParameter("studentId", studentId)
                .setParameter("campusId", campusId)
                .setParameter("slotId", slotId)
                .setParameter("day", day)
                .setParameter("week", week)
                .setParameter("year", year)
                .setParameter("excludeClassId", excludeClassId)
                .getSingleResult();

        if (count2 > 0) return true;

        // Kiểm tra xung đột trong SpecializedTimetable
        String jpql3 = """
        SELECT COUNT(t) FROM SpecializedTimetable t
        JOIN t.specializedClass c
        JOIN Students_SpecializedClasses smc ON c.classId = smc.specializedClass.classId
        WHERE smc.student.id = :studentId
          AND c.creator.campus.campusId = :campusId
          AND t.slot.slotId = :slotId AND t.dayOfWeek = :day
          AND t.weekOfYear = :week AND t.year = :year
        """;
        Long count3 = em.createQuery(jpql3, Long.class)
                .setParameter("studentId", studentId)
                .setParameter("campusId", campusId)
                .setParameter("slotId", slotId)
                .setParameter("day", day)
                .setParameter("week", week)
                .setParameter("year", year)
                .getSingleResult();

        return count3 > 0;
    }

    // === PHÒNG TRỐNG TRONG CAMPUS ===
    private List<Rooms> getAvailableRoomsInCampus(String slotId, DaysOfWeek day, Integer week, Integer year, String campusId) {
        String jpql = """
            SELECT r FROM Rooms r
            WHERE r.campus.campusId = :campusId And r.campus.campusId=:campusId
              AND r.roomId NOT IN (
                SELECT t.room.roomId FROM MajorTimetable t
                JOIN t.classEntity c
                WHERE c.creator.campus.campusId = :campusId
                  AND t.slot.slotId = :slotId AND t.dayOfWeek = :day
                  AND t.weekOfYear = :week AND t.year = :year
                UNION
                SELECT t.room.roomId FROM MinorTimetable t
                JOIN t.minorClass c
                WHERE c.creator.campus.campusId = :campusId
                  AND t.slot.slotId = :slotId AND t.dayOfWeek = :day
                  AND t.weekOfYear = :week AND t.year = :year
                UNION
                SELECT t.room.roomId FROM SpecializedTimetable t
                JOIN t.specializedClass c
                WHERE c.creator.campus.campusId = :campusId
                  AND t.slot.slotId = :slotId AND t.dayOfWeek = :day
                  AND t.weekOfYear = :week AND t.year = :year
              )
            """;
        return em.createQuery(jpql, Rooms.class)
                .setParameter("campusId", campusId)
                .setParameter("slotId", slotId)
                .setParameter("day", day)
                .setParameter("week", week)
                .setParameter("year", year)
                .getResultList();
    }
}