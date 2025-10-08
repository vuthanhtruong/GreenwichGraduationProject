package com.example.demo.TuitionByYear.controller;

import com.example.demo.TuitionByYear.model.TuitionByYear;
import com.example.demo.TuitionByYear.model.TuitionByYearId;
import com.example.demo.TuitionByYear.service.TuitionByYearService;
import com.example.demo.admin.model.Admins;
import com.example.demo.admin.service.AdminsService;
import com.example.demo.campus.model.Campuses;
import com.example.demo.entity.Enums.ContractStatus;
import com.example.demo.subject.model.Subjects;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin-home/annual-restudy-fee")
@PreAuthorize("hasRole('ADMIN')")
public class UpdateAnnualReStudyFeeController {

    private final TuitionByYearService tuitionService;
    private final AdminsService adminsService;

    public UpdateAnnualReStudyFeeController(TuitionByYearService tuitionService,
                                            AdminsService adminsService) {
        this.tuitionService = tuitionService;
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
            if (admissionYear == null || admissionYear < currentYear) {
                redirectAttributes.addFlashAttribute("errorMessage", "Cannot update re-study fees for past years or invalid year.");
                return "redirect:/admin-home/annual-restudy-fee";
            }

            session.setAttribute("admissionYear", admissionYear);

            // Lấy admin và campus
            Admins admin = adminsService.getAdmin();
            Campuses adminCampus = adminsService.getAdminCampus();
            if (adminCampus == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Admin's campus not found.");
                return "redirect:/admin-home/annual-restudy-fee";
            }

            // Lấy danh sách subjects đã có học phí chuẩn (tuition > 0)
            List<TuitionByYear> standardTuitions = tuitionService.getTuitionsWithFeeByYear(admissionYear);
            List<Subjects> eligibleSubjects = standardTuitions.stream()
                    .map(TuitionByYear::getSubject)
                    .toList();

            List<String> errors = new ArrayList<>();
            boolean anyUpdate = false;

            for (Subjects subject : eligibleSubjects) {
                String tuitionKey = "tuitionFee_" + subject.getSubjectId();
                String tuitionValue = allParams.get(tuitionKey);
                if (tuitionValue != null && !tuitionValue.trim().isEmpty()) {
                    try {
                        Double reStudyTuition = Double.parseDouble(tuitionValue);
                        if (reStudyTuition < 0) {
                            errors.add("Re-study fee for " + subject.getSubjectName() + " cannot be negative.");
                            continue;
                        }

                        TuitionByYearId tuitionId = new TuitionByYearId(
                                subject.getSubjectId(), admissionYear, adminCampus.getCampusId()
                        );

                        TuitionByYear existing = tuitionService.findById(tuitionId);

                        if (existing == null) {
                            errors.add("No existing tuition record found for " + subject.getSubjectName() + ". Cannot set re-study fee without standard tuition.");
                            continue;
                        }

                        if (existing.getContractStatus() == ContractStatus.ACTIVE) {
                            errors.add("Cannot update re-study fee for " + subject.getSubjectName() + ": Contract is already finalized.");
                            continue;
                        }

                        existing.setReStudyTuition(reStudyTuition);
                        existing.setCreator(admin);
                        tuitionService.updateTuition(existing);
                        anyUpdate = true;
                    } catch (NumberFormatException e) {
                        errors.add("Invalid re-study fee format for " + subject.getSubjectName());
                    }
                }
            }

            if (!errors.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", String.join("; ", errors));
            }
            if (anyUpdate) {
                redirectAttributes.addFlashAttribute("successMessage", "Re-study fees updated successfully!");
            } else if (errors.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "No re-study fees were updated.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating re-study fees: " + e.getMessage());
        }
        return "redirect:/admin-home/annual-restudy-fee";
    }
}