package com.example.demo.subject.controller;

import com.example.demo.TuitionByYear.model.TuitionByYear;
import com.example.demo.TuitionByYear.service.TuitionByYearService;
import com.example.demo.campus.service.CampusesService;
import com.example.demo.subject.model.Subjects;
import com.example.demo.subject.service.SubjectsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/admin-home/annual-restudy-fee")
@PreAuthorize("hasRole('ADMIN')")
public class ListSubjectsForReStudyController {

    private final TuitionByYearService tuitionService;
    private final CampusesService campusService;

    public ListSubjectsForReStudyController(TuitionByYearService tuitionService, CampusesService campusService) {
        this.tuitionService = tuitionService;
        this.campusService = campusService;
    }

    // Hiển thị trang ban đầu
    @GetMapping
    public String showSubjectsList(Model model, HttpSession session) {
        Integer admissionYear = (Integer) session.getAttribute("admissionYear");
        return listSubjects(model, admissionYear, session);
    }

    @PostMapping
    public String listSubjects(Model model,
                               @RequestParam(value = "admissionYear", required = false) Integer admissionYear,
                               HttpSession session) {
        if (admissionYear != null) {
            session.setAttribute("admissionYear", admissionYear);
        }

        // Lấy tất cả admission years từ TuitionByYear
        List<Integer> admissionYearsFromTuition = tuitionService.findAllAdmissionYears();

        int currentYear = LocalDate.now().getYear();
        List<Integer> futureYears = IntStream.rangeClosed(currentYear, currentYear + 5)
                .boxed()
                .filter(year -> !admissionYearsFromTuition.contains(year))
                .toList();
        admissionYearsFromTuition.addAll(futureYears);

        List<Integer> admissionYears = admissionYearsFromTuition.stream()
                .sorted(Comparator.reverseOrder())
                .toList();

        Integer selectedYear = admissionYear != null ? admissionYear : currentYear;

        // Lấy danh sách TuitionByYear đã có tuition > 0 (học phí chuẩn)
        List<TuitionByYear> eligibleTuitions = tuitionService.getTuitionsWithFeeByYear(selectedYear);

        // Map subjectId → TuitionByYear
        Map<String, TuitionByYear> tuitionMap = eligibleTuitions.stream()
                .collect(Collectors.toMap(t -> t.getId().getSubjectId(), t -> t));

        // Chia 2 danh sách dựa trên reStudyTuition
        List<TuitionByYear> withReStudyFee = eligibleTuitions.stream()
                .filter(t -> t.getReStudyTuition() != null && t.getReStudyTuition() > 0)
                .toList();

        List<TuitionByYear> withoutReStudyFee = eligibleTuitions.stream()
                .filter(t -> t.getReStudyTuition() == null || t.getReStudyTuition() <= 0)
                .toList();

        model.addAttribute("admissionYears", admissionYears);
        model.addAttribute("selectedYear", selectedYear);
        model.addAttribute("withFee", withReStudyFee);
        model.addAttribute("withoutFee", withoutReStudyFee);
        model.addAttribute("tuitionMap", tuitionMap);
        model.addAttribute("Campuses", campusService.getCampuses());

        return "AdminReStudySubjectsList";
    }
}