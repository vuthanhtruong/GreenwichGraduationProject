package com.example.demo.subject.controller;

import com.example.demo.TuitionByYear.model.TuitionByYear;
import com.example.demo.TuitionByYear.service.TuitionByYearService;
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
@RequestMapping("/admin-home")
@PreAuthorize("hasRole('ADMIN')")
public class ListSubjectsController {

    private final TuitionByYearService tuitionService;
    private final SubjectsService subjectService;

    public ListSubjectsController(TuitionByYearService tuitionService, SubjectsService subjectService) {
        this.tuitionService = tuitionService;
        this.subjectService = subjectService;
    }

    // Hiển thị trang ban đầu
    @GetMapping("/subjects-list")
    public String showSubjectsList(Model model, HttpSession session) {
        Integer admissionYear = (Integer) session.getAttribute("admissionYear");
        return listSubjects(model, admissionYear, session);
    }

    @PostMapping("/subjects-list")
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

        // Lấy toàn bộ Subjects
        List<Subjects> subjects = subjectService.getSubjects();

        // Map subjectId → TuitionByYear (nếu có record cho năm đó)
        Map<String, TuitionByYear> tuitionMap = tuitionService.findByAdmissionYear(selectedYear)
                .stream()
                .collect(Collectors.toMap(t -> t.getId().getSubjectId(), t -> t));

        // Chia 2 danh sách
        List<Subjects> withFee = subjects.stream()
                .filter(s -> tuitionMap.containsKey(s.getSubjectId()))
                .toList();

        List<Subjects> withoutFee = subjects.stream()
                .filter(s -> !tuitionMap.containsKey(s.getSubjectId()))
                .toList();

        model.addAttribute("admissionYears", admissionYears);
        model.addAttribute("selectedYear", selectedYear);
        model.addAttribute("withFee", withFee);
        model.addAttribute("withoutFee", withoutFee);
        model.addAttribute("tuitionMap", tuitionMap);

        return "AdminSubjectsList";
    }
}