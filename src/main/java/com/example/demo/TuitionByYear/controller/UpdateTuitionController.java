package com.example.demo.TuitionByYear.controller;


import com.example.demo.TuitionByYear.model.TuitionByYear;
import com.example.demo.TuitionByYear.service.TuitionByYearService;
import com.example.demo.entity.TuitionByYearId;
import com.example.demo.subject.model.Subjects;
import com.example.demo.subject.service.MajorSubjectsService;
import com.example.demo.subject.service.SubjectsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin-home")
@PreAuthorize("hasRole('ADMIN')")
public class UpdateTuitionController {
    private final MajorSubjectsService majorSubjectsService;
    private final TuitionByYearService tuitionService;
    private final SubjectsService subjectService;

    public UpdateTuitionController(MajorSubjectsService majorSubjectsService, TuitionByYearService tuitionService, SubjectsService subjectService) {
        this.majorSubjectsService = majorSubjectsService;
        this.tuitionService = tuitionService;
        this.subjectService = subjectService;
    }

    // Xử lý POST để lưu hoặc cập nhật học phí
    @PostMapping("/update-tuition")
    @Transactional
    public String updateTuition(
            @RequestParam("admissionYear") Integer admissionYear,
            @RequestParam Map<String, String> allParams,
            RedirectAttributes redirectAttributes,
            HttpSession session) {
        try {
            // Kiểm tra nếu admissionYear là năm cũ
            int currentYear = LocalDate.now().getYear();
            if (admissionYear < currentYear) {
                redirectAttributes.addFlashAttribute("errorMessage", "Cannot update tuition fees for past years.");
                return "redirect:/admin-home/subjects-list";
            }
            // Lưu admissionYear vào session
            session.setAttribute("admissionYear", admissionYear);

            // Lấy tất cả môn học để kiểm tra subjectId
            List<Subjects> allSubjects = subjectService.getSubjects();

            // Xử lý từng học phí được gửi từ form
            for (Subjects subject : allSubjects) {
                String tuitionKey = "tuitionFee_" + subject.getSubjectId();
                String tuitionValue = allParams.get(tuitionKey);
                if (tuitionValue != null && !tuitionValue.trim().isEmpty()) {
                    try {
                        Double tuition = Double.parseDouble(tuitionValue);
                        if (tuition < 0) {
                            redirectAttributes.addFlashAttribute("errorMessage", "Tuition fee for " + subject.getSubjectName() + " cannot be negative.");
                            continue;
                        }

                        // Kiểm tra xem TuitionByYear đã tồn tại chưa
                        TuitionByYearId tuitionId = new TuitionByYearId();
                        tuitionId.setAdmissionYear(admissionYear);
                        tuitionId.setSubjectId(subject.getSubjectId());
                        TuitionByYear existingTuition = tuitionService.findById(tuitionId);

                        if (existingTuition != null) {
                            // Cập nhật học phí
                            existingTuition.setTuition(tuition);
                            tuitionService.updateTuition(existingTuition);
                        } else {
                            // Tạo mới TuitionByYear
                            TuitionByYear tuitionByYear = new TuitionByYear();
                            tuitionByYear.setId(tuitionId);
                            tuitionByYear.setSubject(subject);
                            tuitionByYear.setTuition(tuition);
                            tuitionService.createTuition(tuitionByYear);
                        }
                    } catch (NumberFormatException e) {
                        redirectAttributes.addFlashAttribute("errorMessage", "Invalid tuition fee format for " + subject.getSubjectName());
                    }
                }
            }
            redirectAttributes.addFlashAttribute("successMessage", "Tuition fees updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating tuition fees: " + e.getMessage());
        }
        return "redirect:/admin-home/subjects-list";
    }
}
