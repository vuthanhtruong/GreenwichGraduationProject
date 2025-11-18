// src/main/java/com/example/demo/attendance/minorAttendance/controller/MinorLecturerTodayController.java

package com.example.demo.attendance.minorAttendance.controller;

import com.example.demo.timetable.minorTimtable.model.MinorTimetable;
import com.example.demo.timetable.minorTimtable.service.MinorTimetableService;
import com.example.demo.user.minorLecturer.model.MinorLecturers;        // sửa theo entity của bạn
import com.example.demo.user.minorLecturer.service.MinorLecturersService; // sửa theo service của bạn

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/minor-lecturer-home")
public class MinorLecturerTodayController {

    private final MinorTimetableService minorTimetableService;
    private final MinorLecturersService minorLecturersService; // service lấy thông tin giảng viên hiện tại

    public MinorLecturerTodayController(MinorTimetableService minorTimetableService,
                                        MinorLecturersService minorLecturersService) {
        this.minorTimetableService = minorTimetableService;
        this.minorLecturersService = minorLecturersService;
    }

    @GetMapping("/today-minor-attendance")
    public String showTodayMinorAttendance(Model model) {

        // Lấy giảng viên đang đăng nhập (bạn đang dùng cách nào thì giữ nguyên)
        MinorLecturers lecturer = minorLecturersService.getMinorLecturer();
        List<MinorTimetable> todayTimetables = minorTimetableService
                .getMinorTimetableTodayByLecturer(lecturer.getId());
        model.addAttribute("lecturer", lecturer);
        model.addAttribute("todayTimetables", todayTimetables);
        model.addAttribute("today", LocalDate.now()
                .format(DateTimeFormatter.ofPattern("EEEE, dd/MM/yyyy")));
        model.addAttribute("totalSessions", todayTimetables.size());

        return "MinorLecturerTodayAttendance";
    }
}