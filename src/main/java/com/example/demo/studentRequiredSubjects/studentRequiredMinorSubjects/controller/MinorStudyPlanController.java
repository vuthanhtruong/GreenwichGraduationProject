package com.example.demo.studentRequiredSubjects.studentRequiredMinorSubjects.controller;

import com.example.demo.campus.model.Campuses;
import com.example.demo.subject.minorSubject.model.MinorSubjects;
import com.example.demo.tuitionByYear.service.TuitionByYearService;
import com.example.demo.user.deputyStaff.service.DeputyStaffsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/deputy-staff-home")
@PreAuthorize("hasRole('DEPUTY_STAFF')")
public class MinorStudyPlanController {

    private final TuitionByYearService tuitionByYearService;
    private final DeputyStaffsService deputyStaffsService;

    public MinorStudyPlanController(
            TuitionByYearService tuitionByYearService,
            DeputyStaffsService deputyStaffsService) {
        this.tuitionByYearService = tuitionByYearService;
        this.deputyStaffsService = deputyStaffsService;
    }

    @GetMapping("/minor-study-plan")
    public String minorStudyPlan(
            @RequestParam(required = false) Integer admissionYear,
            HttpSession session,
            Model model) {

        Campuses campus = deputyStaffsService.getCampus();
        if (campus == null) {
            model.addAttribute("errorMessage", "Unable to determine your campus.");
            return "DeputyMinorStudyPlan";
        }

        if (admissionYear == null) {
            admissionYear = java.time.Year.now().getValue();
        }

        List<Integer> availableYears = tuitionByYearService.findAllAdmissionYearsWithMinorTuition(campus);
        List<MinorSubjects> subjects = tuitionByYearService.getMinorSubjectsWithTuitionByYear(admissionYear, campus);

        model.addAttribute("subjects", subjects);
        model.addAttribute("availableYears", availableYears);
        model.addAttribute("selectedYear", admissionYear);
        model.addAttribute("totalSubjects", subjects.size());

        session.setAttribute("minorAdmissionYear", admissionYear);

        return "DeputyMinorStudyPlan";
    }

    @PostMapping("/minor-study-plan/filter")
    public String filterByYear(@RequestParam Integer admissionYear, HttpSession session) {
        session.setAttribute("minorAdmissionYear", admissionYear);
        return "redirect:/deputy-staff-home/minor-study-plan?admissionYear=" + admissionYear;
    }

    @PostMapping("/minor-study-plan/go-to-assign")
    public String goToAssign(@RequestParam String subjectId, HttpSession session) {
        session.setAttribute("currentMinorSubjectId", subjectId);
        return "redirect:/deputy-staff-home/minor-study-plan/assign-members";
    }
}