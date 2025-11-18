// src/main/java/com/example/demo/timetable/minorTimetable/controller/MinorTimetableDetailController.java
package com.example.demo.timetable.minorTimtable.controller;

import com.example.demo.attendance.minorAttendance.model.MinorAttendance;
import com.example.demo.attendance.minorAttendance.service.MinorAttendanceService;
import com.example.demo.classes.minorClasses.model.MinorClasses;
import com.example.demo.lecturers_Classes.minorLecturers_MinorClasses.service.MinorLecturers_MinorClassesService;
import com.example.demo.students_Classes.students_MinorClasses.service.StudentsMinorClassesService;
import com.example.demo.timetable.minorTimtable.model.MinorTimetable;
import com.example.demo.timetable.minorTimtable.service.MinorTimetableService;
import com.example.demo.user.employe.model.MinorEmployes;
import com.example.demo.user.employe.service.EmployesService;
import com.example.demo.user.minorLecturer.model.MinorLecturers;
import com.example.demo.user.student.model.Students;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/minor-timetable")
public class MinorTimetableDetailController {

    private final MinorTimetableService timetableService;
    private final MinorLecturers_MinorClassesService lecturersClassesService;
    private final StudentsMinorClassesService studentsClassesService;
    private final MinorAttendanceService attendanceService;
    private final EmployesService employesService;

    public MinorTimetableDetailController(
            MinorTimetableService timetableService,
            MinorLecturers_MinorClassesService lecturersClassesService,
            StudentsMinorClassesService studentsClassesService,
            MinorAttendanceService attendanceService,
            EmployesService employesService) {
        this.timetableService = timetableService;
        this.lecturersClassesService = lecturersClassesService;
        this.studentsClassesService = studentsClassesService;
        this.attendanceService = attendanceService;
        this.employesService = employesService;
    }

    @PostMapping("/detail")
    public String showDetailPost(
            @RequestParam String classId,
            @RequestParam String timetableId,
            Model model,
            HttpSession session) {

        session.setAttribute("current_classId", classId);
        session.setAttribute("current_timetableId", timetableId);

        return processDetail(classId, timetableId, model, session);
    }

    @GetMapping("/detail")
    public String showDetailGet(Model model, HttpSession session) {
        String classId = (String) session.getAttribute("current_classId");
        String timetableId = (String) session.getAttribute("current_timetableId");

        if (classId == null || timetableId == null) {
            model.addAttribute("error", "Session expired or invalid access.");
            return "redirect:/staff-home/classes-list";
        }

        String view = processDetail(classId, timetableId, model, session);
        session.removeAttribute("current_classId");
        session.removeAttribute("current_timetableId");
        return view;
    }

    private String processDetail(String classId, String timetableId, Model model, HttpSession session) {
        MinorEmployes staff = employesService.getMinorEmployee();
        if (staff == null) {
            return "redirect:/login";
        }

        MinorTimetable timetable = timetableService.getMinorTimetableById(timetableId);
        MinorClasses minorClass = timetable.getMinorClass();

        List<MinorLecturers> lecturers = lecturersClassesService.listLecturersInClass(minorClass);
        List<Students> students = studentsClassesService.getStudentsByClass(minorClass);
        List<MinorAttendance> attendances = attendanceService.getAttendanceByTimetable(timetableId);

        List<Object[]> studentAttendanceList = new ArrayList<>();
        for (Students student : students) {
            MinorAttendance attendance = attendances.stream()
                    .filter(a -> a.getStudent().getId().equals(student.getId()))
                    .findFirst()
                    .orElse(null);

            if (attendance == null) {
                attendance = new MinorAttendance();
                attendance.setAttendanceId(java.util.UUID.randomUUID().toString());
                attendance.setStudent(student);
                attendance.setTimetable(timetable);
                attendance.setMarkedBy(staff);
                attendance.setCreatedAt(LocalDateTime.now());
            }
            studentAttendanceList.add(new Object[]{student, attendance});
        }

        model.addAttribute("timetable", timetable);
        model.addAttribute("minorClass", minorClass);
        model.addAttribute("lecturers", lecturers);
        model.addAttribute("studentAttendanceList", studentAttendanceList);
        model.addAttribute("staff", staff);
        if(employesService.getMinorEmployee() instanceof MinorLecturers){
            model.addAttribute("home", "/minor-lecturer-home");
        }
        else {
            model.addAttribute("home", "/deputy-staff-home/minor-classes-list");
        }

        return "MinorTimetableDetail";
    }
}