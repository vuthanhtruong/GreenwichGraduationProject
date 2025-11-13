// src/main/java/com/example/demo/timetable/specializedTimetable/controller/SpecializedTimetableDetailController.java
package com.example.demo.timetable.specializedTimetable.controller;

import com.example.demo.attendance.specializedAttendance.model.SpecializedAttendance;
import com.example.demo.attendance.specializedAttendance.service.SpecializedAttendanceService;
import com.example.demo.classes.specializedClasses.model.SpecializedClasses;
import com.example.demo.lecturers_Classes.majorLecturers_SpecializedClasses.service.MajorLecturers_SpecializedClassesService;
import com.example.demo.students_Classes.students_SpecializedClasses.service.StudentsSpecializedClassesService;
import com.example.demo.timetable.specializedTimetable.model.SpecializedTimetable;
import com.example.demo.timetable.specializedTimetable.service.SpecializedTimetableService;
import com.example.demo.user.employe.model.MajorEmployes;
import com.example.demo.user.employe.service.EmployesService;
import com.example.demo.user.majorLecturer.model.MajorLecturers;
import com.example.demo.user.student.model.Students;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/specialized-timetable")
public class SpecializedTimetableDetailController {

    private final SpecializedTimetableService timetableService;
    private final MajorLecturers_SpecializedClassesService lecturersClassesService;
    private final StudentsSpecializedClassesService studentsClassesService;
    private final SpecializedAttendanceService attendanceService;
    private final EmployesService employesService;

    public SpecializedTimetableDetailController(
            SpecializedTimetableService timetableService,
            MajorLecturers_SpecializedClassesService lecturersClassesService,
            StudentsSpecializedClassesService studentsClassesService,
            SpecializedAttendanceService attendanceService,
            EmployesService employesService) {
        this.timetableService = timetableService;
        this.lecturersClassesService = lecturersClassesService;
        this.studentsClassesService = studentsClassesService;
        this.attendanceService = attendanceService;
        this.employesService = employesService;
    }

    // POST: Open detail page (secure, no ID in URL after redirect)
    @PostMapping("/detail")
    public String showDetailPost(
            @RequestParam String classId,
            @RequestParam String timetableId,
            Model model,
            HttpSession session) {

        // Store IDs in session for safe GET reload
        session.setAttribute("current_classId", classId);
        session.setAttribute("current_timetableId", timetableId);

        return processDetail(classId, timetableId, model, session);
    }

    // GET: Allow page reload or bookmark (no parameters in URL)
    @GetMapping("/detail")
    public String showDetailGet(Model model, HttpSession session) {
        String classId = (String) session.getAttribute("current_classId");
        String timetableId = (String) session.getAttribute("current_timetableId");

        // If session data is missing → invalid access
        if (classId == null || timetableId == null) {
            model.addAttribute("error", "Session expired or invalid access.");
            return "redirect:/staff-home/classes-list";
        }

        String view = processDetail(classId, timetableId, model, session);

        // Optional: Clean up session (recommended)
        session.removeAttribute("current_classId");
        session.removeAttribute("current_timetableId");

        return view;
    }

    // Shared logic for both POST and GET
    private String processDetail(String classId, String timetableId, Model model, HttpSession session) {
        MajorEmployes staff = employesService.getMajorEmployee();
        if (staff == null) {
            return "redirect:/login";
        }

        SpecializedTimetable timetable = timetableService.getTimetableById(timetableId);
        if (timetable == null || !timetable.getSpecializedClass().getClassId().equals(classId)) {
            model.addAttribute("error", "Invalid timetable or class access.");
            return "redirect:/staff-home/classes-list";
        }

        SpecializedClasses specializedClass = timetable.getSpecializedClass();

        // Get lecturers teaching this class
        List<MajorLecturers> lecturers = lecturersClassesService.listLecturersInClass(specializedClass);

        // Get students and their attendance for this timetable
        List<Students> students = studentsClassesService.getStudentsByClass(specializedClass);
        List<SpecializedAttendance> attendances = attendanceService.getAttendanceByTimetable(timetableId);

        List<Object[]> studentAttendanceList = new ArrayList<>();
        for (Students student : students) {
            SpecializedAttendance attendance = attendances.stream()
                    .filter(a -> a.getStudent().getId().equals(student.getId()))
                    .findFirst()
                    .orElse(null);

            if (attendance == null) {
                attendance = new SpecializedAttendance();
                attendance.setStudent(student);
                attendance.setTimetable(timetable);
                attendance.setMarkedBy(staff);
                attendance.setCreatedAt(LocalDateTime.now());
            }
            studentAttendanceList.add(new Object[]{student, attendance});
        }

        // Pass data to view
        model.addAttribute("timetable", timetable);
        model.addAttribute("specializedClass", specializedClass);
        model.addAttribute("lecturers", lecturers);
        model.addAttribute("studentAttendanceList", studentAttendanceList);
        model.addAttribute("staff", staff);

        return "SpecializedTimetableDetail";
    }
}