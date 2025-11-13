// src/main/java/com/example/demo/attendance/specializedAttendance/controller/SpecializedAttendanceController.java
package com.example.demo.attendance.specializedAttendance.controller;

import com.example.demo.attendance.specializedAttendance.model.SpecializedAttendance;
import com.example.demo.attendance.specializedAttendance.service.SpecializedAttendanceService;
import com.example.demo.entity.Enums.AttendanceStatus;
import com.example.demo.timetable.specializedTimetable.model.SpecializedTimetable;
import com.example.demo.timetable.specializedTimetable.service.SpecializedTimetableService;
import com.example.demo.user.employe.model.MajorEmployes;
import com.example.demo.user.employe.service.EmployesService;
import com.example.demo.user.student.service.StudentsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/specialized-timetable")
public class SpecializedAttendanceController {

    private final SpecializedTimetableService timetableService;
    private final SpecializedAttendanceService attendanceService;
    private final EmployesService employesService;
    private final StudentsService studentsService;

    public SpecializedAttendanceController(
            SpecializedTimetableService timetableService,
            SpecializedAttendanceService attendanceService,
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

        // Re-store session for GET reload (safe UX)
        session.setAttribute("current_classId", classId);
        session.setAttribute("current_timetableId", timetableId);

        MajorEmployes staff = employesService.getMajorEmployee();
        if (staff == null) {
            return "redirect:/login";
        }

        SpecializedTimetable timetable = timetableService.getTimetableById(timetableId);

        for (Map.Entry<String, String> entry : allParams.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith("status_")) {
                String studentId = key.substring("status_".length());
                String statusValue = entry.getValue();
                String noteKey = "note_" + studentId;
                String noteValue = allParams.getOrDefault(noteKey, "");

                SpecializedAttendance attendance = attendanceService.findByTimetableAndStudent(timetableId, studentId);
                if (attendance == null) {
                    attendance = new SpecializedAttendance();
                    attendance.setAttendanceId(UUID.randomUUID().toString());
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
        return "redirect:/specialized-timetable/detail";
    }
}