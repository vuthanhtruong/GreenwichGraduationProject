// src/main/java/com/example/demo/attendance/majorAttendance/controller/MajorAttendanceController.java
package com.example.demo.attendance.majorAttendance.controller;

import com.example.demo.attendance.majorAttendance.model.MajorAttendance;
import com.example.demo.attendance.majorAttendance.service.MajorAttendanceService;
import com.example.demo.entity.Enums.AttendanceStatus;
import com.example.demo.timetable.majorTimetable.model.MajorTimetable;
import com.example.demo.timetable.majorTimetable.service.MajorTimetableService;
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
@RequestMapping("/major-timetable")
public class MajorAttendanceController {

    private final MajorTimetableService timetableService;
    private final MajorAttendanceService attendanceService;
    private final EmployesService employesService;
    private final StudentsService studentsService;

    public MajorAttendanceController(
            MajorTimetableService timetableService,
            MajorAttendanceService attendanceService,
            EmployesService employesService, StudentsService studentsService) {
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
            RedirectAttributes redirectAttributes) {

        MajorEmployes staff = employesService.getMajorEmployee();
        MajorTimetable timetable = timetableService.getMajorTimetableById(timetableId);
        for (Map.Entry<String, String> entry : allParams.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith("status_")) {
                String studentId = key.substring("status_".length());
                String statusValue = entry.getValue();
                String noteKey = "note_" + studentId;
                String noteValue = allParams.get(noteKey);

                MajorAttendance attendance = attendanceService.findByTimetableAndStudent(timetableId, studentId);
                if (attendance == null) {
                    attendance = new MajorAttendance();
                    attendance.setAttendanceId(UUID.randomUUID().toString());
                    attendance.setStudent(studentsService.getStudentById(studentId));
                    attendance.setTimetable(timetable);
                    attendance.setMarkedBy(staff);
                }

                attendance.setStatus("ATTENDED".equals(statusValue) ? AttendanceStatus.ATTENDED :
                        "ABSENT".equals(statusValue) ? AttendanceStatus.ABSENT : null);
                attendance.setNote(noteValue);
                attendance.setCreatedAt(LocalDateTime.now());

                attendanceService.save(attendance);
            }
        }

        redirectAttributes.addFlashAttribute("success", "Attendance saved successfully!");
        return "redirect:/major-timetable/detail";
    }
}