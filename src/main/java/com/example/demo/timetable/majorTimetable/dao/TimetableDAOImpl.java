// com.example.demo.timtable.majorTimetable.dao.TimetableDAOImpl
package com.example.demo.timetable.majorTimetable.dao;

import com.example.demo.timetable.majorTimetable.model.Timetable;
import com.example.demo.timetable.majorTimetable.service.MajorTimetableService;
import com.example.demo.timetable.minorTimtable.service.MinorTimetableService;
import com.example.demo.timetable.specializedTimetable.service.SpecializedTimetableService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional
public class TimetableDAOImpl implements TimetableDAO {
    @Override
    public List<Timetable> getTimetableTodayByLecturer(String lecturerId) {
        List<Timetable> timetableList = new ArrayList<>();
        timetableList.addAll(majorTimetableService.getMajorTimetableTodayByLecturer(lecturerId));
        timetableList.addAll(specializedTimetableService.getSpecializedTimetableTodayByLecturer(lecturerId));
        return timetableList;
    }

    @Override
    public List<Timetable> getMajorLecturerTimetable(String lecturerId, Integer week, Integer year) {
        List<Timetable> timetables = new ArrayList<>();
        timetables.addAll(majorTimetableService.getMajorTimetablesByLecturer(lecturerId, week, year));
        timetables.addAll(specializedTimetableService.getSpecializedTimetablesByMajorLecturer(lecturerId, week, year));
        return timetables;
    }

    @Override
    public List<Timetable> getTimetablesByWeekInYear(Integer weekInYear, Integer year) {
        return List.of();
    }

    @Override
    public List<Timetable> getStudentTimetable(String studentId, Integer week, Integer year) {
        List<Timetable> timetables = new ArrayList<>();
        timetables.addAll(majorTimetableService.getMajorTimetableByStudent(studentId, week, year));
        timetables.addAll(specializedTimetableService.getSpecializedTimetableByStudent(studentId,week,year));
        timetables.addAll(minorTimetableService.getMinorTimetableByStudent(studentId,week,year));
        return timetables;
    }

    private final MajorTimetableService majorTimetableService;
    private final SpecializedTimetableService specializedTimetableService;
    private final MinorTimetableService minorTimetableService;

    public TimetableDAOImpl(MajorTimetableService majorTimetableService, SpecializedTimetableService specializedTimetableService, MinorTimetableService minorTimetableService) {
        this.majorTimetableService = majorTimetableService;
        this.specializedTimetableService = specializedTimetableService;
        this.minorTimetableService = minorTimetableService;
    }


    @PersistenceContext
    private EntityManager em;
}