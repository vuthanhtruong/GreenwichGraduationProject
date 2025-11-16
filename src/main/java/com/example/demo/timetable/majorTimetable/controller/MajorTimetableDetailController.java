// src/main/java/com/example/demo/timtable/majorTimetable/controller/MajorTimetableDetailController.java
package com.example.demo.timetable.majorTimetable.controller;

import com.example.demo.attendance.majorAttendance.model.MajorAttendance;
import com.example.demo.attendance.majorAttendance.service.MajorAttendanceService;
import com.example.demo.classes.majorClasses.model.MajorClasses;
import com.example.demo.lecturers_Classes.majorLecturers_MajorClasses.service.MajorLecturers_MajorClassesService;
import com.example.demo.students_Classes.students_MajorClass.service.StudentsMajorClassesService;
import com.example.demo.timetable.majorTimetable.model.MajorTimetable;
import com.example.demo.timetable.majorTimetable.service.MajorTimetableService;
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
@RequestMapping("/major-timetable")
public class MajorTimetableDetailController {

    private final MajorTimetableService timetableService;
    private final MajorLecturers_MajorClassesService lecturersClassesService;
    private final StudentsMajorClassesService studentsClassesService;
    private final MajorAttendanceService attendanceService;
    private final EmployesService employesService;

    public MajorTimetableDetailController(
            MajorTimetableService timetableService,
            MajorLecturers_MajorClassesService lecturersClassesService,
            StudentsMajorClassesService studentsClassesService,
            MajorAttendanceService attendanceService,
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
        return view;
    }

    // Shared logic for both POST and GET
    private String processDetail(String classId, String timetableId, Model model, HttpSession session) {
        MajorEmployes staff = employesService.getMajorEmployee();
        if (staff == null) {
            return "redirect:/login";
        }

        MajorTimetable timetable = timetableService.getMajorTimetableById(timetableId);
        MajorClasses majorClass = timetable.getClassEntity();

        // Get lecturers teaching this class
        List<MajorLecturers> lecturers = lecturersClassesService.listLecturersInClass(majorClass);

        // Get students and their attendance for this timetable
        List<Students> students = studentsClassesService.getStudentsByClass(majorClass);
        List<MajorAttendance> attendances = attendanceService.getAttendanceByTimetable(timetableId);

        List<Object[]> studentAttendanceList = new ArrayList<>();
        for (Students student : students) {
            MajorAttendance attendance = attendances.stream()
                    .filter(a -> a.getStudent().getId().equals(student.getId()))
                    .findFirst()
                    .orElse(null);

            if (attendance == null) {
                attendance = new MajorAttendance();
                attendance.setStudent(student);
                attendance.setTimetable(timetable);
                attendance.setMarkedBy(staff);
                attendance.setCreatedAt(LocalDateTime.now());
            }
            studentAttendanceList.add(new Object[]{student, attendance});
        }
        if(staff instanceof MajorLecturers){
            model.addAttribute("before", "/major-lecturer-home/major-timetable");
        }
        else{
            model.addAttribute("before", "/staff-home/classes-list");
        }

        // Pass data to view
        model.addAttribute("timetable", timetable);
        model.addAttribute("majorClass", majorClass);
        model.addAttribute("lecturers", lecturers);
        model.addAttribute("studentAttendanceList", studentAttendanceList);
        model.addAttribute("staff", staff);
        return "MajorTimetableDetail";
    }
}