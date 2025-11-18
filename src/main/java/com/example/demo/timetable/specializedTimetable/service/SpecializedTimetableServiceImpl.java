package com.example.demo.timetable.specializedTimetable.service;

import com.example.demo.classes.specializedClasses.model.SpecializedClasses;
import com.example.demo.entity.Enums.DaysOfWeek;
import com.example.demo.room.model.Rooms;
import com.example.demo.timetable.specializedTimetable.dao.SpecializedTimetableDAO;
import com.example.demo.timetable.specializedTimetable.model.SpecializedTimetable;
import com.example.demo.timetable.majorTimetable.model.Slots;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SpecializedTimetableServiceImpl implements SpecializedTimetableService {
    @Override
    public List<SpecializedTimetable> getAllSpecializedTimetablesInWeek(Integer weekOfYear, Integer year, String campusId) {
        return dao.getAllSpecializedTimetablesInWeek(weekOfYear, year, campusId);
    }

    @Override
    public List<SpecializedTimetable> getSpecializedTimetableByStudentAndClassId(String studentId, String classId) {
        return dao.getSpecializedTimetableByStudentAndClassId(studentId, classId);
    }

    @Override
    public List<SpecializedTimetable> getSpecializedTimetableTodayByLecturer(String lecturerId) {
        return dao.getSpecializedTimetableTodayByLecturer(lecturerId);
    }

    @Override
    public List<SpecializedClasses> getSpecializedClassesBySpecializedTimetable(Integer week, Integer year, String campusId) {
        return dao.getSpecializedClassesBySpecializedTimetable(week, year, campusId);
    }

    @Override
    public SpecializedTimetable getTimetableById(String timetableId) {
        return dao.getTimetableById(timetableId);
    }

    @Override
    public List<SpecializedTimetable> getSpecializedTimetablesByMajorLecturer(String lecturerId, Integer week, Integer year) {
        return dao.getSpecializedTimetablesByMajorLecturer(lecturerId, week, year);
    }

    @Override
    public List<SpecializedTimetable> getSpecializedTimetableByStudent(String studentId, Integer week, Integer year) {
        return dao.getSpecializedTimetableByStudent(studentId, week, year);
    }

    @Override
    public SpecializedTimetable getTimetableByClassSlotDayWeek(String classId, String campusId, String slotId, DaysOfWeek dayOfWeek, Integer weekOfYear, Integer year) {
        return dao.getTimetableByClassSlotDayWeek(classId, campusId, slotId, dayOfWeek, weekOfYear, year);
    }

    private final SpecializedTimetableDAO dao;


    @Override
    public List<Rooms> getAvailableRoomsForSlot(
            String classId, String campusId, Slots slots, DaysOfWeek daysOfWeek, Integer weekNumberInYear, Integer year) {
        return dao.getAvailableRoomsForSlot(classId, campusId, slots, daysOfWeek, weekNumberInYear, year);
    }

    @Override
    public String[][] SlotOfTheDayThatCanBeSuccessfullyArranged(
            String classId, String campusId, Slots slots, DaysOfWeek daysOfWeek, Integer weekNumberInYear, Integer year) {
        return dao.SlotOfTheDayThatCanBeSuccessfullyArranged(classId, campusId, slots, daysOfWeek, weekNumberInYear, year);
    }

    @Override
    public void saveSpecializedTimetable(SpecializedTimetable timetable, String campusId) {
        dao.saveSpecializedTimetable(timetable, campusId);
    }

    @Override
    public List<SpecializedTimetable> getSpecializedTimetablesByWeekInYear(Integer weekInYear, Integer year, String campusId) {
        return dao.getSpecializedTimetablesByWeekInYear(weekInYear, year, campusId);
    }

    @Override
    public void delete(SpecializedTimetable timetable) {
        dao.delete(timetable);
    }

    @Override
    public SpecializedTimetable getById(String timetableId) {
        return dao.getById(timetableId);
    }

    @Override
    public int countBookedSlotsInWeek(String classId, Integer week, Integer year, String campusId) {
        return dao.countBookedSlotsInWeek(classId, week, year, campusId);
    }

    @Override
    public int countTotalBookedSlots(String classId) {
        return dao.countTotalBookedSlots(classId);
    }

    @Override
    public List<SpecializedTimetable> getAllSchedulesByClass(String classId) {
        return dao.getAllSchedulesByClass(classId);
    }
}