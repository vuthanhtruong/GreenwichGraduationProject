// src/main/java/com/example/demo/staff/controller/StaffMinorLecturerEvaluationController.java
package com.example.demo.lecturerEvaluations.controller;

import com.example.demo.lecturerEvaluations.model.MinorLecturerEvaluations;
import com.example.demo.lecturerEvaluations.service.LecturerEvaluationService;
import com.example.demo.user.deputyStaff.service.DeputyStaffsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/deputy-staff-home")
public class DeputyStaffMinorLecturerEvaluationController {

    private final LecturerEvaluationService evaluationService;
    private final DeputyStaffsService deputyStaffsService;

    public DeputyStaffMinorLecturerEvaluationController(LecturerEvaluationService evaluationService, DeputyStaffsService deputyStaffsService) {
        this.evaluationService = evaluationService;
        this.deputyStaffsService = deputyStaffsService;
    }

    @GetMapping("/student-evaluate-minor")
    public String showMinorLecturerEvaluations(Model model) {
        List<MinorLecturerEvaluations> evaluations = evaluationService.findAllMinorLecturerEvaluationsByCampus(deputyStaffsService.getCampus().getCampusId());

        model.addAttribute("evaluations", evaluations);
        model.addAttribute("total", evaluations.size());

        return "StaffStudentEvaluateMinorList"; // templates/staff/StaffStudentEvaluateMinorList.html
    }
}