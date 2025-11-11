// com.example.demo.timtable.majorTimetable.dao.TimetableDAOImpl
package com.example.demo.timtable.majorTimetable.dao;

import com.example.demo.timtable.majorTimetable.model.Timetable;
import com.example.demo.timtable.majorTimetable.model.MajorTimetable;
import com.example.demo.timtable.majorTimetable.service.MajorTimetableService;
import com.example.demo.timtable.specializedTimetable.model.SpecializedTimetable;
import com.example.demo.timtable.specializedTimetable.service.SpecializedTimetableService;
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
    public List<Timetable> getTimetablesByWeekInYear(Integer weekInYear, Integer year) {
        return List.of();
    }

    @Override
    public List<Timetable> getStudentTimetable(String studentId, Integer week, Integer year) {
        List<Timetable> timetables = new ArrayList<>();
        timetables.addAll(majorTimetableService.getMajorTimetableByStudent(studentId, week, year));
        timetables.addAll(specializedTimetableService.getSpecializedTimetableByStudent(studentId,week,year));
        return timetables;
    }

    private final MajorTimetableService majorTimetableService;
    private final SpecializedTimetableService specializedTimetableService;

    public TimetableDAOImpl(MajorTimetableService majorTimetableService, SpecializedTimetableService specializedTimetableService) {
        this.majorTimetableService = majorTimetableService;
        this.specializedTimetableService = specializedTimetableService;
    }


    @PersistenceContext
    private EntityManager em;




}