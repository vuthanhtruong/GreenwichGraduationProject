// src/main/java/com/example/demo/timetable/student/controller/StudentTimetableDetailController.java

package com.example.demo.timetable.majorTimetable.controller;

import com.example.demo.classes.majorClasses.model.MajorClasses;
import com.example.demo.classes.majorClasses.service.MajorClassesService;
import com.example.demo.classes.minorClasses.service.MinorClassesService;
import com.example.demo.classes.specializedClasses.model.SpecializedClasses;
import com.example.demo.classes.specializedClasses.service.SpecializedClassesService;

import com.example.demo.lecturers_Classes.majorLecturers_MajorClasses.service.MajorLecturers_MajorClassesService;
import com.example.demo.lecturers_Classes.minorLecturers_MinorClasses.service.MinorLecturers_MinorClassesService;
import com.example.demo.lecturers_Classes.majorLecturers_SpecializedClasses.service.MajorLecturers_SpecializedClassesService;

import com.example.demo.students_Classes.students_MajorClass.service.StudentsMajorClassesService;
import com.example.demo.students_Classes.students_MinorClasses.service.StudentsMinorClassesService;
import com.example.demo.students_Classes.students_SpecializedClasses.service.StudentsSpecializedClassesService;

import com.example.demo.timetable.majorTimetable.model.Timetable;
import com.example.demo.timetable.majorTimetable.service.MajorTimetableService;
import com.example.demo.timetable.minorTimtable.service.MinorTimetableService;
import com.example.demo.timetable.specializedTimetable.service.SpecializedTimetableService;

import com.example.demo.user.student.model.Students;
import com.example.demo.user.student.service.StudentsService;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;

@Controller
public class StudentTimetableDetailController {

    private final MajorTimetableService majorTimetableService;
    private final MinorTimetableService minorTimetableService;
    private final SpecializedTimetableService specializedTimetableService;

    private final MajorLecturers_MajorClassesService majorLecturersService;
    private final MinorLecturers_MinorClassesService minorLecturersService;
    private final MajorLecturers_SpecializedClassesService specializedLecturersService;

    private final StudentsMajorClassesService majorStudentsService;
    private final StudentsMinorClassesService minorStudentsService;
    private final StudentsSpecializedClassesService specializedStudentsService;

    private final MajorClassesService majorClassesService;
    private final MinorClassesService minorClassesService;
    private final SpecializedClassesService specializedClassesService;

    private final StudentsService studentsService;

    public StudentTimetableDetailController(
            MajorTimetableService majorTimetableService,
            MinorTimetableService minorTimetableService,
            SpecializedTimetableService specializedTimetableService,

            MajorLecturers_MajorClassesService majorLecturersService,
            MinorLecturers_MinorClassesService minorLecturersService,
            MajorLecturers_SpecializedClassesService specializedLecturersService,

            StudentsMajorClassesService majorStudentsService,
            StudentsMinorClassesService minorStudentsService,
            StudentsSpecializedClassesService specializedStudentsService,

            MajorClassesService majorClassesService,
            MinorClassesService minorClassesService,
            SpecializedClassesService specializedClassesService,

            StudentsService studentsService) {

        this.majorTimetableService = majorTimetableService;
        this.minorTimetableService = minorTimetableService;
        this.specializedTimetableService = specializedTimetableService;

        this.majorLecturersService = majorLecturersService;
        this.minorLecturersService = minorLecturersService;
        this.specializedLecturersService = specializedLecturersService;

        this.majorStudentsService = majorStudentsService;
        this.minorStudentsService = minorStudentsService;
        this.specializedStudentsService = specializedStudentsService;

        this.majorClassesService = majorClassesService;
        this.minorClassesService = minorClassesService;
        this.specializedClassesService = specializedClassesService;
        this.studentsService = studentsService;
    }

    @PostMapping("/student-home/timetable/detail")
    public String openDetail(@RequestParam String classId,
                             @RequestParam(required = false) String timetableId,
                             HttpSession session) {
        if (timetableId == null || timetableId.isBlank()) {
            return "redirect:/student-home/timetable";
        }
        session.setAttribute("detail_classId", classId);
        session.setAttribute("detail_timetableId", timetableId);
        return "redirect:/student-home/timetable/detail";
    }

    @GetMapping("/student-home/timetable/detail")
    public String showDetail(Model model, HttpSession session) {
        String classId = (String) session.getAttribute("detail_classId");
        String timetableId = (String) session.getAttribute("detail_timetableId");

        session.removeAttribute("detail_classId");
        session.removeAttribute("detail_timetableId");

        // Kiểm tra đăng nhập
        if (studentsService.getStudent() == null) {
            return "redirect:/login";
        }

        Timetable timetable = findTimetable(timetableId);
        if (timetable == null || !timetable.getClassId().equals(classId)) {
            return "redirect:/student-home/timetable";
        }

        model.addAttribute("timetable", timetable);
        model.addAttribute("className", timetable.getClassName());
        model.addAttribute("type", timetable.getClassType().toLowerCase()); // major / minor / specialized
        model.addAttribute("creatorName", timetable.getCreatorName());

        // Lấy danh sách giảng viên và sinh viên theo loại lớp
        model.addAttribute("lecturers", getLecturersByClassId(classId));
        model.addAttribute("students", getStudentsByClassId(classId));

        return "TimetableDetailStudentView";
    }

    private Timetable findTimetable(String id) {
        Timetable t = majorTimetableService.getById(id);
        if (t != null) return t;
        t = minorTimetableService.getById(id);
        if (t != null) return t;
        return specializedTimetableService.getById(id);
    }

    // ==============================================
    // LẤY DANH SÁCH GIẢNG VIÊN THEO LOẠI LỚP
    // ==============================================
    private List<?> getLecturersByClassId(String classId) {
        // Major class
        MajorClasses majorClass = majorClassesService.getClassById(classId);
        if (majorClass != null) {
            return majorLecturersService.listLecturersInClass(majorClass);
        }

        // Minor class (giả sử MinorClasses có method getClassById tương tự)
        var minorClass = minorClassesService.getClassById(classId);
        if (minorClass != null) {
            return minorLecturersService.listLecturersInClass(minorClass);
        }

        // Specialized class
        SpecializedClasses specializedClass = specializedClassesService.getClassById(classId);
        if (specializedClass != null) {
            return specializedLecturersService.listLecturersInClass(specializedClass);
        }

        return Collections.emptyList();
    }

    // ==============================================
    // LẤY DANH SÁCH SINH VIÊN THEO LOẠI LỚP
    // ==============================================
    private List<Students> getStudentsByClassId(String classId) {
        // Major
        MajorClasses majorClass = majorClassesService.getClassById(classId);
        if (majorClass != null) {
            return majorStudentsService.getStudentsByClass(majorClass);
        }

        // Minor
        var minorClass = minorClassesService.getClassById(classId);
        if (minorClass != null) {
            return minorStudentsService.getStudentsByClass(minorClass);
        }

        // Specialized
        SpecializedClasses specializedClass = specializedClassesService.getClassById(classId);
        if (specializedClass != null) {
            return specializedStudentsService.getStudentsByClass(specializedClass);
        }

        return Collections.emptyList();
    }
}