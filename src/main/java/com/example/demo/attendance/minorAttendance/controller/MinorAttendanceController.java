// src/main/java/com/example/demo/attendance/minorAttendance/controller/MinorAttendanceController.java
package com.example.demo.attendance.minorAttendance.controller;

import com.example.demo.attendance.minorAttendance.model.MinorAttendance;
import com.example.demo.attendance.minorAttendance.service.MinorAttendanceService;
import com.example.demo.entity.Enums.AttendanceStatus;
import com.example.demo.timetable.minorTimtable.model.MinorTimetable;
import com.example.demo.timetable.minorTimtable.service.MinorTimetableService;
import com.example.demo.user.employe.model.MinorEmployes;
import com.example.demo.user.employe.service.EmployesService;
import com.example.demo.user.student.service.StudentsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Map;

@Controller
@RequestMapping("/minor-timetable")
public class MinorAttendanceController {

    private final MinorTimetableService timetableService;
    private final MinorAttendanceService attendanceService;
    private final EmployesService employesService;
    private final StudentsService studentsService;

    public MinorAttendanceController(
            MinorTimetableService timetableService,
            MinorAttendanceService attendanceService,
            EmployesService employesService,
            StudentsService studentsService) {
        this.timetableService = timetableService;
        this.attendanceService = attendanceService;
        this.employesService = employesService;
        this.studentsService = studentsService;
    }

    @PostMapping("/detail/save-attendance")
    public String saveAttendance(
            @RequestParam String timetableId,
            @RequestParam String classId,
            @RequestParam Map<String, String> allParams,
            RedirectAttributes redirectAttributes,
            jakarta.servlet.http.HttpSession session) {

        session.setAttribute("current_classId", classId);
        session.setAttribute("current_timetableId", timetableId);

        MinorEmployes staff = employesService.getMinorEmployee();
        if (staff == null) {
            return "redirect:/login";
        }

        MinorTimetable timetable = timetableService.getMinorTimetableById(timetableId);

        for (Map.Entry<String, String> entry : allParams.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith("status_")) {
                String studentId = key.substring("status_".length());
                String statusValue = entry.getValue();
                String noteKey = "note_" + studentId;
                String noteValue = allParams.getOrDefault(noteKey, "");

                MinorAttendance attendance = attendanceService.findByTimetableAndStudent(timetableId, studentId);
                if (attendance == null) {
                    attendance = new MinorAttendance();
                    attendance.setAttendanceId(java.util.UUID.randomUUID().toString());
                    attendance.setStudent(studentsService.getStudentById(studentId));
                    attendance.setTimetable(timetable);
                    attendance.setMarkedBy(staff);
                    attendance.setCreatedAt(LocalDateTime.now());
                }

                attendance.setStatus("ATTENDED".equals(statusValue) ? AttendanceStatus.ATTENDED :
                        "ABSENT".equals(statusValue) ? AttendanceStatus.ABSENT : null);
                attendance.setNote(noteValue);

                attendanceService.save(attendance);
            }
        }

        redirectAttributes.addFlashAttribute("success", "Attendance saved successfully!");
        return "redirect:/minor-timetable/detail";
    }
}