package com.example.demo.student_scholarship.controller;

import com.example.demo.scholarship.model.Scholarships;
import com.example.demo.student_scholarship.model.Students_Scholarships;
import com.example.demo.scholarshipByYear.model.ScholarshipByYear;
import com.example.demo.scholarshipByYear.service.ScholarshipByYearService;
import com.example.demo.scholarship.service.ScholarshipsService;
import com.example.demo.student_scholarship.service.StudentScholarshipService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/staff-home/award-scholarships")
@PreAuthorize("hasRole('STAFF')")
public class ListAwardScholarshipsController {

    private final ScholarshipsService scholarshipsService;
    private final ScholarshipByYearService scholarshipByYearService;
    private final StudentScholarshipService studentScholarshipService;

    @Autowired
    public ListAwardScholarshipsController(
            ScholarshipsService scholarshipsService,
            ScholarshipByYearService scholarshipByYearService,
            StudentScholarshipService studentScholarshipService) {
        this.scholarshipsService = scholarshipsService;
        this.scholarshipByYearService = scholarshipByYearService;
        this.studentScholarshipService = studentScholarshipService;
    }

    @GetMapping("")
    public String showAwardScholarships(Model model, HttpSession session) {
        Integer admissionYear = (Integer) session.getAttribute("awardAdmissionYear");
        if (admissionYear == null) {
            admissionYear = LocalDate.now().getYear();
            session.setAttribute("awardAdmissionYear", admissionYear);
        }
        model.addAttribute("studentsScholarship", new Students_Scholarships());
        return listAwardedScholarships(model, admissionYear, session);
    }

    @PostMapping("")
    public String listAwardedScholarships(
            Model model,
            @RequestParam(value = "admissionYear", required = false) Integer admissionYear,
            HttpSession session) {
        try {
            // Xử lý admissionYear
            Integer selectedYear = admissionYear;
            if (selectedYear == null) {
                selectedYear = (Integer) session.getAttribute("awardAdmissionYear");
                if (selectedYear == null) {
                    selectedYear = LocalDate.now().getYear();
                }
            }
            session.setAttribute("awardAdmissionYear", selectedYear);

            // Lấy danh sách năm nhập học
            List<Integer> admissionYears = scholarshipByYearService.getAllAdmissionYears();
            int currentYear = LocalDate.now().getYear();
            // Sử dụng biến tạm để đảm bảo effectively final
            final List<Integer> existingAdmissionYears = admissionYears;
            List<Integer> futureYears = IntStream.rangeClosed(currentYear, currentYear + 5)
                    .boxed()
                    .filter(year -> !existingAdmissionYears.contains(year))
                    .collect(Collectors.toList());

            // Gộp và sắp xếp danh sách năm
            admissionYears.addAll(futureYears);
            admissionYears = admissionYears.stream()
                    .sorted(Comparator.reverseOrder())
                    .collect(Collectors.toList());

            // Lấy danh sách học bổng
            List<Scholarships> availableScholarships = scholarshipsService.getAllScholarships();

            // Tính toán số lượng còn lại cho mỗi học bổng
            Map<String, Long> remainingCounts = new HashMap<>();
            for (Scholarships scholarship : availableScholarships) {
                String scholarshipId = scholarship.getScholarshipId();
                ScholarshipByYear scholarshipByYear = scholarshipByYearService.getScholarshipByYear(scholarshipId, selectedYear);
                Double amount = (scholarshipByYear != null) ? scholarshipByYear.getAmount() : 0.0;
                Long awardedCount = studentScholarshipService.getCountStudentScholarshipByYear(selectedYear, scholarship);
                Long remaining = Math.max(0, amount.longValue() - awardedCount);
                remainingCounts.put(scholarshipId, remaining);
            }

            // Lấy danh sách học bổng đã cấp
            Map<String, Map<String, Object>> awardedScholarships =
                    studentScholarshipService.getAwardedScholarshipsByYear(selectedYear);

            // Thêm dữ liệu vào model
            model.addAttribute("admissionYears", admissionYears);
            model.addAttribute("selectedYear", selectedYear);
            model.addAttribute("availableScholarships", availableScholarships);
            model.addAttribute("awardedScholarships", awardedScholarships);
            model.addAttribute("remainingCounts", remainingCounts);
            model.addAttribute("studentsScholarship", new Students_Scholarships());

            return "AwardScholarships";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Failed to load awarded scholarships: " + e.getMessage());
            model.addAttribute("admissionYears", List.of());
            model.addAttribute("selectedYear", LocalDate.now().getYear());
            model.addAttribute("availableScholarships", List.of());
            model.addAttribute("awardedScholarships", Map.of());
            model.addAttribute("remainingCounts", Map.of());
            model.addAttribute("studentsScholarship", new Students_Scholarships());
            return "AwardScholarships";
        }
    }
}