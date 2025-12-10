package com.example.demo.tuitionByYear.controller;

import com.example.demo.campus.model.Campuses;
import com.example.demo.campus.service.CampusesService;
import com.example.demo.entity.Enums.ActivityStatus;
import com.example.demo.entity.Enums.ContractStatus;
import com.example.demo.scholarship.model.Scholarships;
import com.example.demo.scholarship.service.ScholarshipsService;
import com.example.demo.scholarshipByYear.model.ScholarshipByYear;
import com.example.demo.scholarshipByYear.service.ScholarshipByYearService;
import com.example.demo.tuitionByYear.model.TuitionByYear;
import com.example.demo.tuitionByYear.service.TuitionByYearService;
import com.example.demo.user.admin.service.AdminsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin-home/contracts-list")
@PreAuthorize("hasRole('ADMIN')")
public class ContractsListController {

    private final TuitionByYearService tuitionService;
    private final ScholarshipByYearService scholarshipByYearService;
    private final ScholarshipsService scholarshipsService;
    private final CampusesService campusesService;
    private final AdminsService adminsService;

    public ContractsListController(TuitionByYearService tuitionService,
                                   ScholarshipByYearService scholarshipByYearService,
                                   ScholarshipsService scholarshipsService,
                                   CampusesService campusesService,
                                   AdminsService adminsService) {
        this.tuitionService = tuitionService;
        this.scholarshipByYearService = scholarshipByYearService;
        this.scholarshipsService = scholarshipsService;
        this.campusesService = campusesService;
        this.adminsService = adminsService;
    }

    // === Hiển thị trang ===
    @GetMapping
    public String showPage(Model model, HttpSession session) {
        Integer year = (Integer) session.getAttribute("admissionYear");
        int currentYear = LocalDate.now().getYear();
        return loadPage(model, year != null ? year : currentYear, session);
    }

    @PostMapping
    public String changeYear(@RequestParam(value = "admissionYear", required = false) Integer admissionYear,
                             HttpSession session, Model model) {
        int year = (admissionYear != null) ? admissionYear : LocalDate.now().getYear();
        session.setAttribute("admissionYear", year);
        return loadPage(model, year, session);
    }

    private String loadPage(Model model, int selectedYear, HttpSession session) {
        Campuses campus = adminsService.getAdminCampus();

        // Lấy tất cả các năm có dữ liệu (tuition + scholarship)
        Set<Integer> years = new HashSet<>();
        years.addAll(tuitionService.findAllAdmissionYears(campus));
        years.addAll(scholarshipByYearService.getAllAdmissionYears());

        // Thêm 5 năm tương lai để admin có thể tạo trước
        int current = LocalDate.now().getYear();
        for (int i = 0; i <= 5; i++) years.add(current + i);

        List<Integer> admissionYears = years.stream()
                .sorted(Comparator.reverseOrder())
                .toList();

        // Dữ liệu học phí
        List<TuitionByYear> tuitionsWithFee = tuitionService.getTuitionsWithFeeByYearAndCampus(selectedYear, campus);

        // Dữ liệu học bổng
        List<Scholarships> allScholarships = scholarshipsService.getAllScholarships();
        List<ScholarshipByYear> scholarshipByYears = scholarshipByYearService.getScholarshipsByYear(selectedYear);

        Map<String, ScholarshipByYear> scholarshipByYearMap = scholarshipByYears.stream()
                .collect(Collectors.toMap(
                        s -> s.getId().getScholarshipId(),
                        s -> s,
                        (a, b) -> a
                ));

        model.addAttribute("admissionYears", admissionYears);
        model.addAttribute("selectedYear", selectedYear);
        model.addAttribute("tuitionsWithFee", tuitionsWithFee);
        model.addAttribute("allScholarships", allScholarships);
        model.addAttribute("scholarshipByYearMap", scholarshipByYearMap);

        return "AdminContractsList"; // tên file HTML
    }

    // === CHỐT HỢP ĐỒNG TỪNG MỤC ĐÃ CHỌN ===
    @PostMapping("/finalize")
    public String finalizeSelectedContracts(
            @RequestParam("admissionYear") Integer admissionYear,
            @RequestParam(value = "tuitionIds", required = false) List<String> tuitionIds,
            @RequestParam(value = "scholarshipIds", required = false) List<String> scholarshipIds,
            RedirectAttributes ra,
            HttpSession session) {

        if (admissionYear == null) {
            ra.addFlashAttribute("errorMessage", "Academic year is required.");
            return "redirect:/admin-home/contracts-list";
        }

        Campuses campus = adminsService.getAdminCampus();
        List<String> errors = new ArrayList<>();
        int activatedCount = 0;

        // 1. Xử lý học phí
        if (tuitionIds != null && !tuitionIds.isEmpty()) {
            for (String subjectId : tuitionIds) {
                TuitionByYear tuition = tuitionService.getTuitionBySubjectAndYear(subjectId, admissionYear, campus);

                if (tuition == null) {
                    errors.add("Subject not found: " + subjectId);
                    continue;
                }
                if (ContractStatus.ACTIVE.equals(tuition.getContractStatus())) {
                    continue; // Đã active rồi, bỏ qua
                }
                if (tuition.getTuition() == null || tuition.getTuition() <= 0) {
                    errors.add("Tuition fee invalid: " + subjectId);
                    continue;
                }
                if (tuition.getReStudyTuition() == null || tuition.getReStudyTuition() <= 0) {
                    errors.add("Re-study fee invalid: " + subjectId);
                    continue;
                }

                tuition.setContractStatus(ContractStatus.ACTIVE);
                tuitionService.save(tuition);
                activatedCount++;
            }
        }

        // 2. Xử lý học bổng
        if (scholarshipIds != null && !scholarshipIds.isEmpty()) {
            for (String sid : scholarshipIds) {
                ScholarshipByYear scholarship = scholarshipByYearService.getByScholarshipIdAndYear(sid, admissionYear);

                if (scholarship == null) {
                    errors.add("Scholarship not found: " + sid);
                    continue;
                }
                if (ContractStatus.ACTIVE.equals(scholarship.getStatus())) {
                    continue;
                }
                if (scholarship.getAmount() == null || scholarship.getAmount() <= 0) {
                    errors.add("Amount invalid for scholarship: " + sid);
                    continue;
                }
                if (scholarship.getDiscountPercentage() == null || scholarship.getDiscountPercentage() <= 0) {
                    errors.add("Discount percentage invalid for scholarship: " + sid);
                    continue;
                }

                scholarship.setContractStatus(ContractStatus.ACTIVE);     // Đây là chốt hợp đồng
                scholarship.setStatus(ActivityStatus.ACTIVATED);          // Kích hoạt học bổng (nếu cần)
                scholarshipByYearService.save(scholarship);
                activatedCount++;
            }
        }

        // 3. Trả thông báo
        if (!errors.isEmpty()) {
            ra.addFlashAttribute("errorMessage", "Some items could not be activated: " + String.join("; ", errors));
        } else if (activatedCount > 0) {
            ra.addFlashAttribute("successMessage", "Successfully activated " + activatedCount + " contract(s)!");
        } else {
            ra.addFlashAttribute("infoMessage", "No contracts were activated (already active or no selection).");
        }

        session.setAttribute("admissionYear", admissionYear);
        return "redirect:/admin-home/contracts-list";
    }
}