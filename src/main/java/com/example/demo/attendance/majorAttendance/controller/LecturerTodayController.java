// src/main/java/com/example/demo/timetable/majorTimetable/controller/LecturerTodayController.java
package com.example.demo.attendance.majorAttendance.controller;

import com.example.demo.timetable.majorTimetable.model.Timetable;
import com.example.demo.timetable.majorTimetable.service.TimetableService;
import com.example.demo.user.majorLecturer.model.MajorLecturers;
import com.example.demo.user.majorLecturer.service.MajorLecturersService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/major-lecturer-home")
public class LecturerTodayController {

    private final TimetableService timetableService;
    private final MajorLecturersService lecturersService;

    public LecturerTodayController(TimetableService timetableService, MajorLecturersService lecturersService) {
        this.timetableService = timetableService;
        this.lecturersService = lecturersService;
    }

    @GetMapping("/today-attendance")
    public String showTodayAttendance(Model model) {

        MajorLecturers lecturer=lecturersService.getMajorLecturer();

        List<Timetable> todayTimetables = timetableService.getTimetableTodayByLecturer(lecturer.getId());

        model.addAttribute("lecturer", lecturer);
        model.addAttribute("todayTimetables", todayTimetables);
        model.addAttribute("today", LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, dd/MM/yyyy")));
        model.addAttribute("totalSessions", todayTimetables.size());

        return "LecturerTodayAttendance";
    }
}