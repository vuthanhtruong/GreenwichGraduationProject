// src/main/java/com/example/demo/student/controller/StudentAttendanceController.java
package com.example.demo.attendance.majorAttendance.controller;

import com.example.demo.timetable.majorTimetable.model.Timetable;
import com.example.demo.timetable.majorTimetable.service.MajorTimetableService;
import com.example.demo.timetable.minorTimtable.service.MinorTimetableService;
import com.example.demo.timetable.specializedTimetable.service.SpecializedTimetableService;
import com.example.demo.user.student.model.Students;
import com.example.demo.user.student.service.StudentsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/student-home")
public class StudentAttendanceController {

    private final MajorTimetableService majorService;
    private final MinorTimetableService minorService;
    private final SpecializedTimetableService specializedService;
    private final StudentsService studentsService;

    public StudentAttendanceController(MajorTimetableService majorService,
                                       MinorTimetableService minorService,
                                       SpecializedTimetableService specializedService, StudentsService studentsService) {
        this.majorService = majorService;
        this.minorService = minorService;
        this.specializedService = specializedService;
        this.studentsService = studentsService;
    }

    @GetMapping("/student-classes-list/attendance")
    public String showAttendanceList(
            @RequestParam String classId,
            HttpSession session,
            Model model) {

        Students student = studentsService.getStudent();

        List<Timetable> timetableList = getTimetablesByClassAndStudent(classId, student.getId());

        // === 3. TRUYỀN DỮ LIỆU ===
        model.addAttribute("student", student);
        model.addAttribute("classId", classId);
        model.addAttribute("timetables", timetableList); // Danh sách
        model.addAttribute("action", "/student-home/student-classes-list/attendance?classId=" + classId);

        return "StudentAttendanceListByClass"; // HTML danh sách
    }

    private List<Timetable> getTimetablesByClassAndStudent(String classId, String studentId) {
        var major = majorService.getMajorTimetableByStudentAndClassId(studentId, classId);
        if (!major.isEmpty()) return new ArrayList<>(major);

        var minor = minorService.getMinorTimetableByStudentAndClassId(studentId, classId);
        if (!minor.isEmpty()) return new ArrayList<>(minor);

        var spec = specializedService.getSpecializedTimetableByStudentAndClassId(studentId, classId);
        if (!spec.isEmpty()) return new ArrayList<>(spec);

        return List.of();
    }
}