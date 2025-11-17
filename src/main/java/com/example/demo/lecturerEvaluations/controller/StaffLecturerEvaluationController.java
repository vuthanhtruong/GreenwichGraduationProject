// src/main/java/com/example/demo/staff/controller/StaffLecturerEvaluationController.java
package com.example.demo.staff.controller;

import com.example.demo.lecturerEvaluations.model.MajorLecturerEvaluations;
import com.example.demo.lecturerEvaluations.service.LecturerEvaluationService;
import com.example.demo.user.majorLecturer.service.MajorLecturersService;
import com.example.demo.user.staff.service.StaffsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/staff-home")
public class StaffLecturerEvaluationController {

    private final LecturerEvaluationService evaluationService;
    private final StaffsService staffsService;

    public StaffLecturerEvaluationController(
            LecturerEvaluationService evaluationService, StaffsService staffsService) {
        this.evaluationService = evaluationService;
        this.staffsService = staffsService;
    }

    @GetMapping("/student-evaluate")
    public String showMajorLecturerEvaluations(Model model) {

        // Lấy toàn bộ đánh giá của Major Lecturer (có fetch reviewer + class + lecturer)
        List<MajorLecturerEvaluations> evaluations = evaluationService.findAllMajorLecturerEvaluationsByCampus(staffsService.getCampusOfStaff().getCampusId());

        model.addAttribute("evaluations", evaluations);
        model.addAttribute("total", evaluations.size());

        return "StaffStudentEvaluateMajorList"; // template ở src/main/resources/templates/staff/StudentEvaluateMajorList.html
    }
}