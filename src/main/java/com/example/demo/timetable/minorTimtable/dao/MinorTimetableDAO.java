// src/main/java/com/example/demo/timtable/minorTimetable/dao/MinorTimetableDAO.java
package com.example.demo.timetable.minorTimtable.dao;

import com.example.demo.classes.minorClasses.model.MinorClasses;
import com.example.demo.entity.Enums.DaysOfWeek;
import com.example.demo.room.model.Rooms;
import com.example.demo.timetable.majorTimetable.model.Slots;
import com.example.demo.timetable.minorTimtable.model.MinorTimetable;

import java.util.List;

public interface MinorTimetableDAO {

    List<MinorClasses> getMinorClassesByMinorTimetable(Integer week, Integer year, String campusId);

    List<MinorTimetable> getAllSchedulesByClass(String classId);

    int countTotalBookedSlots(String classId);

    int countBookedSlotsInWeek(String classId, Integer week, Integer year, String campusId);

    MinorTimetable getById(String timetableId);

    void delete(MinorTimetable timetable);

    List<MinorTimetable> getMinorTimetablesByWeekInYear(Integer week, Integer year, String campusId);

    MinorTimetable getTimetableByClassSlotDayWeek(
            String classId, String campusId, String slotId, DaysOfWeek dayOfWeek, Integer weekOfYear, Integer year);

    List<Rooms> getAvailableRoomsForSlot(
            String classId, String campusId, Slots slot, DaysOfWeek day, Integer week, Integer year);

    String[][] SlotOfTheDayThatCanBeSuccessfullyArranged(
            String classId, String campusId, Slots slots, DaysOfWeek daysOfWeek, Integer weekNumberInYear, Integer year);

    void saveMinorTimetable(MinorTimetable timetable, String campusId);

    List<MinorTimetable> getMinorTimetableByStudent(String studentId, Integer week, Integer year);
    List<MinorTimetable> getMinorTimetablesByMinorLecturer(String lecturerId, Integer week, Integer year);
    MinorTimetable getMinorTimetableById(String timetableId);
    List<MinorTimetable> getMinorTimetableByStudentAndClassId(
            String studentId, String classId);
    List<MinorTimetable> getAllMinorTimetablesInWeek(Integer weekOfYear, Integer year, String campusId);
    List<MinorTimetable> getMinorTimetableTodayByLecturer(String lecturerId);
    void sendScheduleNotification(String classId);
    // Trong MinorTimetableDAO.java (interface)
    Object[] getDashboardSummaryMinor(String campusId, Integer weekOfYear, Integer year);
    List<Object[]> getTop5BusyLecturersMinor(String campusId, Integer weekOfYear, Integer year);
    long[] getSlotsPerDayOfWeekMinor(String campusId, Integer weekOfYear, Integer year);
    List<Object[]> getTop5UsedRoomsMinor(String campusId, Integer weekOfYear, Integer year);
    long getUnscheduledMinorClassesCount(String campusId, Integer weekOfYear, Integer year);
}