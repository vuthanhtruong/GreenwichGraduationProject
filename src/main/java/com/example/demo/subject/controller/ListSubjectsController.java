package com.example.demo.subject.controller;

import com.example.demo.TuitionByYear.service.TuitionByYearService;
import com.example.demo.TuitionByYear.model.TuitionByYear;
import com.example.demo.subject.model.Subjects;
import com.example.demo.subject.service.MajorSubjectsService;
import com.example.demo.subject.service.SubjectsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin-home")
@PreAuthorize("hasRole('ADMIN')")
public class ListSubjectsController {

    private final MajorSubjectsService majorsubjectsService;
    private final TuitionByYearService tuitionService;
    private final SubjectsService subjectService;

    @Autowired
    public ListSubjectsController(MajorSubjectsService majorsubjectsService, TuitionByYearService tuitionService, SubjectsService subjectService) {
        this.majorsubjectsService = majorsubjectsService;
        this.tuitionService = tuitionService;
        this.subjectService = subjectService;
    }

    @GetMapping("/subjects-list")
    public String listSubjects(Model model, @RequestParam(value = "admissionYear", required = false) Integer admissionYear) {
        // Lấy danh sách tất cả admission years duy nhất từ TuitionByYear
        List<Integer> admissionYears = tuitionService.getAllAdmissionYears()
                .stream()
                .sorted(Comparator.reverseOrder()) // Sắp xếp giảm dần để năm mới nhất lên đầu
                .collect(Collectors.toList());

        // Nếu không có admissionYear được chọn, lấy năm mới nhất
        Integer selectedYear = admissionYear != null ? admissionYear :
                (admissionYears.isEmpty() ? LocalDate.now().getYear() : admissionYears.get(0));

        // Lấy danh sách môn học và học phí theo admissionYear
        List<Subjects> subjects = subjectService.getSubjects();
        List<TuitionByYear> tuitions = tuitionService.getTuitionsByYear(selectedYear);

        // Thêm dữ liệu vào model
        model.addAttribute("subjects", subjects);
        model.addAttribute("tuitions", tuitions);
        model.addAttribute("admissionYears", admissionYears);
        model.addAttribute("selectedYear", selectedYear);

        return "SubjectsList";
    }
}