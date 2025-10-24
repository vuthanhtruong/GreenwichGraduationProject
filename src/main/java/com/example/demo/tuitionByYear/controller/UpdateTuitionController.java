package com.example.demo.tuitionByYear.controller;

import com.example.demo.tuitionByYear.model.TuitionByYear;
import com.example.demo.tuitionByYear.model.TuitionByYearId;
import com.example.demo.tuitionByYear.service.TuitionByYearService;
import com.example.demo.user.admin.model.Admins;
import com.example.demo.user.admin.service.AdminsService;
import com.example.demo.campus.model.Campuses;
import com.example.demo.entity.Enums.ContractStatus;
import com.example.demo.subject.abstractSubject.model.Subjects;
import com.example.demo.subject.abstractSubject.service.SubjectsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin-home")
@PreAuthorize("hasRole('ADMIN')")
public class UpdateTuitionController {

    private final TuitionByYearService tuitionService;
    private final SubjectsService subjectService;
    private final AdminsService adminsService;

    public UpdateTuitionController(TuitionByYearService tuitionService,
                                   SubjectsService subjectService, AdminsService adminsService) {
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
                return "redirect:/admin-home/tuition-management";
            }

            session.setAttribute("admissionYear", admissionYear);

            // Lấy admin và campus
            Admins admin = adminsService.getAdmin();
            Campuses adminCampus = adminsService.getAdminCampus();
            if (adminCampus == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Admin's campus not found.");
                return "redirect:/admin-home/tuition-management";
            }

            List<Subjects> allSubjects = subjectService.getSubjects();
            List<String> errors = new ArrayList<>();
            boolean anyUpdate = false;

            for (Subjects subject : allSubjects) {
                String tuitionKey = "tuitionFee_" + subject.getSubjectId();
                String tuitionValue = allParams.get(tuitionKey);
                if (tuitionValue != null && !tuitionValue.trim().isEmpty()) {
                    try {
                        Double tuition = Double.parseDouble(tuitionValue);
                        if (tuition < 0) {
                            errors.add("Tuition fee for " + subject.getSubjectName() + " cannot be negative.");
                            continue;
                        }

                        TuitionByYearId tuitionId = new TuitionByYearId(
                                subject.getSubjectId(), admissionYear, adminCampus.getCampusId()
                        );

                        TuitionByYear existing = tuitionService.findById(tuitionId);

                        if (existing != null && existing.getContractStatus() == ContractStatus.ACTIVE) {
                            errors.add("Cannot update tuition fee for " + subject.getSubjectName() + ": Contract is already finalized.");
                            continue;
                        }

                        if (existing != null) {
                            existing.setTuition(tuition);
                            existing.setCreator(admin);
                            existing.setCampus(adminCampus);
                            existing.setSubject(subject);
                            tuitionService.updateTuition(existing);
                        } else {
                            TuitionByYear tuitionByYear = new TuitionByYear();
                            tuitionByYear.setId(tuitionId);
                            tuitionByYear.setTuition(tuition);
                            tuitionByYear.setCreator(admin);
                            tuitionByYear.setCampus(adminCampus);
                            tuitionByYear.setSubject(subject);
                            tuitionByYear.setContractStatus(ContractStatus.DRAFT);
                            tuitionService.createTuition(tuitionByYear);
                        }
                        anyUpdate = true;
                    } catch (NumberFormatException e) {
                        errors.add("Invalid tuition fee format for " + subject.getSubjectName());
                    }
                }
            }

            if (!errors.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", String.join("; ", errors));
            }
            if (anyUpdate) {
                redirectAttributes.addFlashAttribute("successMessage", "Tuition fees updated successfully!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating tuition fees: " + e.getMessage());
        }
        return "redirect:/admin-home/tuition-management";
    }
}