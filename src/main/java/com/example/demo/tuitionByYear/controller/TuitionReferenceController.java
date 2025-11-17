package com.example.demo.tuitionByYear.controller;

import com.example.demo.campus.model.Campuses;
import com.example.demo.tuitionByYear.model.TuitionByYear;
import com.example.demo.tuitionByYear.service.TuitionByYearService;
import com.example.demo.user.student.model.Students;
import com.example.demo.user.student.service.StudentsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/student-home")
public class TuitionReferenceController {

    private final StudentsService studentsService;
    private final TuitionByYearService tuitionByYearService;

    public TuitionReferenceController(StudentsService studentsService,
                                      TuitionByYearService tuitionByYearService) {
        this.studentsService = studentsService;
        this.tuitionByYearService = tuitionByYearService;
    }

    @GetMapping("/tuition-reference")
    public String showTuitionReference(Model model) {
        Students student = studentsService.getStudent();

        Integer admissionYear = student.getAdmissionYear();
        Campuses campus = student.getCampus();

        List<TuitionByYear> tuitions = tuitionByYearService
                .tuitionReferenceForStudentsByCampus(admissionYear, campus);

        model.addAttribute("admissionYear", admissionYear);
        model.addAttribute("campusName", campus.getCampusName());
        model.addAttribute("tuitions", tuitions);

        return "StudentTuitionReference";
    }
}