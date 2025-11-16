package com.example.demo.timetable.majorTimetable.service;

import com.example.demo.classes.majorClasses.model.MajorClasses;
import com.example.demo.entity.Enums.DaysOfWeek;
import com.example.demo.room.model.Rooms;
import com.example.demo.timetable.majorTimetable.dao.MajorTimetableDAO;
import com.example.demo.timetable.majorTimetable.model.MajorTimetable;
import com.example.demo.timetable.majorTimetable.model.Slots;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MajorTimetableServiceImpl implements MajorTimetableService {
    @Override
    public List<MajorTimetable> getMajorTimetableByStudentAndClassId(String studentId, String classId) {
        return majorTimetableDAO.getMajorTimetableByStudentAndClassId(studentId, classId);
    }

    @Override
    public List<MajorTimetable> getMajorTimetableTodayByLecturer(String lecturerId) {
        return majorTimetableDAO.getMajorTimetableTodayByLecturer(lecturerId);
    }

    @Override
    public List<MajorClasses> getMajorClassesByMajorTimetable(Integer week, Integer year, String campusId) {
        return majorTimetableDAO.getMajorClassesByMajorTimetable(week, year, campusId);
    }

    @Override
    public MajorTimetable getMajorTimetableById(String timetableId) {
        return majorTimetableDAO.getMajorTimetableById(timetableId);
    }

    @Override
    public List<MajorTimetable> getMajorTimetablesByLecturer(String lecturerId, Integer week, Integer year) {
        return majorTimetableDAO.getMajorTimetablesByLecturer(lecturerId, week, year);
    }

    @Override
    public List<MajorTimetable> getMajorTimetableByStudent(String studentId, Integer week, Integer year) {
        return majorTimetableDAO.getMajorTimetableByStudent(studentId, week, year);
    }

    @Override
    public List<MajorTimetable> getAllSchedulesByClass(String classId) {
        return majorTimetableDAO.getAllSchedulesByClass(classId);
    }

    @Override
    public int countTotalBookedSlots(String classId) {
        return majorTimetableDAO.countTotalBookedSlots(classId);
    }

    @Override
    public int countBookedSlotsInWeek(String classId, Integer week, Integer year, String campusId) {
        return majorTimetableDAO.countBookedSlotsInWeek(classId, week, year, campusId);
    }

    @Override
    public MajorTimetable getById(String timetableId) {
        return majorTimetableDAO.getById(timetableId);
    }

    @Override
    public void delete(MajorTimetable timetable) {
        majorTimetableDAO.delete(timetable);
    }

    private final MajorTimetableDAO majorTimetableDAO;

    public MajorTimetableServiceImpl(MajorTimetableDAO majorTimetableDAO) {
        this.majorTimetableDAO = majorTimetableDAO;
    }

    @Override
    public List<MajorTimetable> getMajorTimetablesByWeekInYear(Integer weekInYear, Integer year, String campusId) {
        return majorTimetableDAO.getMajorTimetablesByWeekInYear(weekInYear, year, campusId);
    }

    @Override
    public MajorTimetable getTimetableByClassSlotDayWeek(String classId, String campusId, String slotId, DaysOfWeek dayOfWeek, Integer weekOfYear, Integer year) {
        return majorTimetableDAO.getTimetableByClassSlotDayWeek(classId, campusId, slotId, dayOfWeek, weekOfYear, year);
    }

    @Override
    public List<Rooms> getAvailableRoomsForSlot(String classId, String campusId, Slots slots, DaysOfWeek daysOfWeek, Integer weekNumberInYear, Integer year) {
        return majorTimetableDAO.getAvailableRoomsForSlot(classId, campusId, slots, daysOfWeek, weekNumberInYear, year);
    }

    @Override
    public String[][] SlotOfTheDayThatCanBeSuccessfullyArranged(String classId, String campusId, Slots slots, DaysOfWeek daysOfWeek, Integer weekNumberInYear, Integer year) {
        return majorTimetableDAO.SlotOfTheDayThatCanBeSuccessfullyArranged(classId, campusId, slots, daysOfWeek, weekNumberInYear, year);
    }

    @Override
    public void SaveMajorTimetable(MajorTimetable timetable, String campusId) {
        majorTimetableDAO.SaveMajorTimetable(timetable, campusId);
    }
}