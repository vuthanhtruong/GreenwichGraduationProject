package com.example.demo.subject.controller;

import com.example.demo.TuitionByYear.model.TuitionByYear;
import com.example.demo.TuitionByYear.service.TuitionByYearService;
import com.example.demo.subject.model.Subjects;
import com.example.demo.subject.service.MajorSubjectsService;
import com.example.demo.subject.service.SubjectsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/admin-home")
@PreAuthorize("hasRole('ADMIN')")
public class ListSubjectsController {

    private final MajorSubjectsService majorSubjectsService;
    private final TuitionByYearService tuitionService;
    private final SubjectsService subjectService;

    @Autowired
    public ListSubjectsController(MajorSubjectsService majorSubjectsService, TuitionByYearService tuitionService, SubjectsService subjectService) {
        this.majorSubjectsService = majorSubjectsService;
        this.tuitionService = tuitionService;
        this.subjectService = subjectService;
    }

    // Hiển thị trang ban đầu
    @GetMapping("/subjects-list")
    public String showSubjectsList(Model model, HttpSession session) {
        Integer admissionYear = (Integer) session.getAttribute("admissionYear");
        return listSubjects(model, admissionYear, session);
    }

    // Xử lý form POST khi chọn admissionYear
    @PostMapping("/subjects-list")
    public String listSubjects(Model model, @RequestParam(value = "admissionYear", required = false) Integer admissionYear, HttpSession session) {
        // Lưu admissionYear vào session
        if (admissionYear != null) {
            session.setAttribute("admissionYear", admissionYear);
        }
        // Lấy danh sách tất cả admission years duy nhất từ TuitionByYear
        List<Integer> admissionYearsFromTuition = tuitionService.getAllAdmissionYears();
        // Thêm năm hiện tại và 5 năm tương lai (từ năm hiện tại đến +5)
        int currentYear = LocalDate.now().getYear();
        List<Integer> futureYears = IntStream.rangeClosed(currentYear, currentYear + 5)
                .boxed()
                .filter(year -> !admissionYearsFromTuition.contains(year))
                .collect(Collectors.toList());
        admissionYearsFromTuition.addAll(futureYears);
        // Sắp xếp lại danh sách theo thứ tự giảm dần
        List<Integer> admissionYears = admissionYearsFromTuition.stream()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
        // Nếu không có admissionYear được chọn, mặc định là năm hiện tại
        Integer selectedYear = admissionYear != null ? admissionYear : currentYear;
        // Lấy tất cả môn học và sắp xếp theo semester, rồi subjectId
        List<Subjects> allSubjects = subjectService.getSubjects();
        // Lấy danh sách TuitionByYear cho admissionYear được chọn
        List<TuitionByYear> tuitionByYears = tuitionService.getTuitionsByYear(selectedYear);
        // Tạo map để tra cứu tuitionFee theo subjectId
        Map<String, Double> tuitionFeeMap = tuitionByYears.stream()
                .collect(Collectors.toMap(
                        t -> t.getId().getSubjectId(),
                        TuitionByYear::getTuition,
                        (existing, replacement) -> existing // Giữ giá trị đầu tiên nếu có trùng lặp
                ));

        // Thêm dữ liệu vào model
        model.addAttribute("allSubjects", allSubjects);
        model.addAttribute("admissionYears", admissionYears);
        model.addAttribute("selectedYear", selectedYear);
        model.addAttribute("tuitionFeeMap", tuitionFeeMap);

        return "AdminSubjectsList";
    }
}