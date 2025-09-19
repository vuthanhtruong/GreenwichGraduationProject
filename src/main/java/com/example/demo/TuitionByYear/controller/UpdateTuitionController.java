package com.example.demo.TuitionByYear.controller;

import com.example.demo.TuitionByYear.model.TuitionByYear;
import com.example.demo.TuitionByYear.model.TuitionByYearId;
import com.example.demo.TuitionByYear.service.TuitionByYearService;
import com.example.demo.admin.model.Admins;
import com.example.demo.admin.service.AdminsService;
import com.example.demo.campus.model.Campuses;
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
    private final AdminsService adminsService;

    public UpdateTuitionController(MajorSubjectsService majorSubjectsService, TuitionByYearService tuitionService,
                                   SubjectsService subjectService, AdminsService adminsService) {
        this.majorSubjectsService = majorSubjectsService;
        this.tuitionService = tuitionService;
        this.subjectService = subjectService;
        this.adminsService = adminsService;
    }

    @PostMapping("/update-tuition")
    @Transactional
    public String updateTuition(
            @RequestParam("admissionYear") Integer admissionYear,
            @RequestParam Map<String, String> allParams,
            RedirectAttributes redirectAttributes,
            HttpSession session) {
        try {
            int currentYear = LocalDate.now().getYear();
            if (admissionYear < currentYear) {
                redirectAttributes.addFlashAttribute("errorMessage", "Cannot update tuition fees for past years.");
                return "redirect:/admin-home/subjects-list";
            }

            session.setAttribute("admissionYear", admissionYear);

            // Lấy admin và campus
            Admins admin = adminsService.getAdmin();
            Campuses adminCampus = adminsService.getAdminCampus();
            if (adminCampus == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Admin's campus not found.");
                return "redirect:/admin-home/subjects-list";
            }

            List<Subjects> allSubjects = subjectService.getSubjects();

            for (Subjects subject : allSubjects) {
                String tuitionKey = "tuitionFee_" + subject.getSubjectId();
                String tuitionValue = allParams.get(tuitionKey);
                if (tuitionValue != null && !tuitionValue.trim().isEmpty()) {
                    try {
                        Double tuition = Double.parseDouble(tuitionValue);
                        if (tuition < 0) {
                            redirectAttributes.addFlashAttribute("errorMessage",
                                    "Tuition fee for " + subject.getSubjectName() + " cannot be negative.");
                            continue;
                        }

                        TuitionByYearId tuitionId = new TuitionByYearId(
                                subject.getSubjectId(), admissionYear, adminCampus.getCampusId()
                        );

                        TuitionByYear existing = tuitionService.findById(tuitionId);

                        if (existing != null) {
                            existing.setTuition(tuition);
                            existing.setCreator(admin);
                            existing.setCampus(adminCampus);
                            existing.setSubject(subject);
                            tuitionService.updateTuition(existing);
                        } else {
                            TuitionByYear tuitionByYear = new TuitionByYear();
                            tuitionByYear.setId(tuitionId); // Explicitly set the ID
                            tuitionByYear.setTuition(tuition);
                            tuitionByYear.setCreator(admin);
                            tuitionByYear.setCampus(adminCampus);
                            tuitionByYear.setSubject(subject);
                            tuitionService.createTuition(tuitionByYear);
                        }
                    } catch (NumberFormatException e) {
                        redirectAttributes.addFlashAttribute("errorMessage",
                                "Invalid tuition fee format for " + subject.getSubjectName());
                    }
                }
            }
            redirectAttributes.addFlashAttribute("successMessage", "Tuition fees updated successfully!");
        } catch (Exception e) {
            e.printStackTrace(); // log lỗi gốc
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating tuition fees: " + e.getMessage());
        }
        return "redirect:/admin-home/subjects-list";
    }
}