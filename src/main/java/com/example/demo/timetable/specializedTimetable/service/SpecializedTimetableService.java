package com.example.demo.timetable.specializedTimetable.service;

import com.example.demo.classes.specializedClasses.model.SpecializedClasses;
import com.example.demo.entity.Enums.DaysOfWeek;
import com.example.demo.room.model.Rooms;
import com.example.demo.timetable.majorTimetable.model.Slots;
import com.example.demo.timetable.specializedTimetable.model.SpecializedTimetable;

import java.util.List;

public interface SpecializedTimetableService {
    // Kiểm tra lịch đã tồn tại chưa
    SpecializedTimetable getTimetableByClassSlotDayWeek(
            String classId, String campusId, String slotId, DaysOfWeek dayOfWeek, Integer weekOfYear, Integer year);

    // Lấy danh sách phòng còn trống cho 1 slot
    List<Rooms> getAvailableRoomsForSlot(
            String classId, String campusId, Slots slots, DaysOfWeek daysOfWeek, Integer weekNumberInYear, Integer year);

    // Ma trận gợi ý phòng cho 1 ngày (7 ngày x 6 slot)
    String[][] SlotOfTheDayThatCanBeSuccessfullyArranged(
            String classId, String campusId, Slots slots, DaysOfWeek daysOfWeek, Integer WeekNumberInYear, Integer year);

    // Lưu lịch
    void saveSpecializedTimetable(SpecializedTimetable timetable, String campusId);

    // Lấy tất cả lịch trong 1 tuần
    List<SpecializedTimetable> getSpecializedTimetablesByWeekInYear(Integer weekInYear, Integer year, String campusId);

    // Xóa
    void delete(SpecializedTimetable timetable);

    // Lấy theo ID
    SpecializedTimetable getById(String timetableId);

    // Đếm slot đã đặt trong tuần
    int countBookedSlotsInWeek(String classId, Integer week, Integer year, String campusId);

    // Đếm tổng slot đã đặt (toàn khóa)
    int countTotalBookedSlots(String classId);

    // Lấy toàn bộ lịch của lớp (không phân trang)
    List<SpecializedTimetable> getAllSchedulesByClass(String classId);
    List<SpecializedTimetable> getSpecializedTimetableByStudent(String studentId, Integer week, Integer year);
    List<SpecializedTimetable> getSpecializedTimetablesByMajorLecturer(String lecturerId, Integer week, Integer year);
    SpecializedTimetable getTimetableById(String timetableId);
    List<SpecializedClasses> getSpecializedClassesBySpecializedTimetable(Integer week, Integer year, String campusId);
    List<SpecializedTimetable> getSpecializedTimetableTodayByLecturer(String lecturerId);
    List<SpecializedTimetable> getSpecializedTimetableByStudentAndClassId(
            String studentId, String classId);
    void sendScheduleNotification(String classId);
    List<SpecializedTimetable> getAllSpecializedTimetablesInWeek(Integer weekOfYear, Integer year, String campusId);
    Object[] getDashboardSummarySpecialized(String campusId, Integer weekOfYear, Integer year);
    List<Object[]> getTop5BusyLecturersSpecialized(String campusId, Integer weekOfYear, Integer year);
    long[] getSlotsPerDayOfWeekSpecialized(String campusId, Integer weekOfYear, Integer year);
    List<Object[]> getTop5UsedRoomsSpecialized(String campusId, Integer weekOfYear, Integer year);
    int getUnscheduledSpecializedClassesCount(String campusId, Integer weekOfYear, Integer year);
}
