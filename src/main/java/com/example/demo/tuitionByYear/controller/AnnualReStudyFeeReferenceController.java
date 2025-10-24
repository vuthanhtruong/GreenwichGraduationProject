package com.example.demo.tuitionByYear.controller;

import com.example.demo.tuitionByYear.model.TuitionByYear;
import com.example.demo.tuitionByYear.service.TuitionByYearService;
import com.example.demo.user.admin.service.AdminsService;
import com.example.demo.campus.model.Campuses;
import com.example.demo.campus.service.CampusesService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/admin-home/annual-restudy-fee/reference")
@PreAuthorize("hasRole('ADMIN')")
@Transactional
public class AnnualReStudyFeeReferenceController {

    private final TuitionByYearService tuitionByYearService;
    private final CampusesService campusesService;
    private final AdminsService adminsService;

    public AnnualReStudyFeeReferenceController(TuitionByYearService tuitionByYearService,
                                               CampusesService campusesService,
                                               AdminsService adminsService) {
        this.tuitionByYearService = tuitionByYearService;
        this.campusesService = campusesService;
        this.adminsService = adminsService;
    }

    private void populateCommonAttributes(Model model, HttpSession session) {
        Campuses adminCampus = adminsService.getAdminCampus();
        List<Campuses> campuses = campusesService.getCampuses();
        List<Integer> admissionYears = tuitionByYearService.findAllAdmissionYears();
        model.addAttribute("Campuses", campuses);
        model.addAttribute("admissionYears", admissionYears);
        model.addAttribute("campus", adminCampus);
    }

    /** Hiển thị trang tham khảo học phí học lại */
    @GetMapping
    public String showTuitionReferencePage(Model model, HttpSession session) {
        populateCommonAttributes(model, session);

        String campusId = (String) session.getAttribute("selectedCampusId");
        Integer selectedYear = (Integer) session.getAttribute("selectedYear");

        if (campusId != null && selectedYear != null) {
            Campuses referenceCampus = campusesService.getCampusById(campusId);
            if (referenceCampus != null) {
                List<TuitionByYear> referenceTuitions =
                        tuitionByYearService.tuitionFeesByCampus(campusId, selectedYear);
                model.addAttribute("referenceCampus", referenceCampus);
                model.addAttribute("referenceTuitions", referenceTuitions);
                model.addAttribute("selectedCampusId", campusId);
                model.addAttribute("selectedYear", selectedYear);
            } else {
                model.addAttribute("errorMessage", "Selected campus not found.");
            }
        }

        return "AnnualReStudyFeesByCampus";
    }

    /** Xử lý POST khi chọn campus + admissionYear */
    @PostMapping
    public String filterTuitionFees(@RequestParam("campusId") String campusId,
                                    @RequestParam("admissionYear") Integer admissionYear,
                                    Model model,
                                    HttpSession session) {
        populateCommonAttributes(model, session);

        if (campusId == null || campusId.trim().isEmpty()) {
            model.addAttribute("errorMessage", "Please select a valid campus.");
            return "AnnualReStudyFeesByCampus";
        }
        if (admissionYear == null) {
            model.addAttribute("errorMessage", "Please select a valid admission year.");
            return "AnnualReStudyFeesByCampus";
        }

        Campuses referenceCampus = campusesService.getCampusById(campusId);
        if (referenceCampus == null) {
            model.addAttribute("errorMessage", "Selected campus not found.");
            return "AnnualReStudyFeesByCampus";
        }

        session.setAttribute("selectedCampusId", campusId);
        session.setAttribute("selectedYear", admissionYear);

        List<TuitionByYear> referenceTuitions =
                tuitionByYearService.tuitionFeesByCampus(campusId, admissionYear);
        if (referenceTuitions.isEmpty()) {
            model.addAttribute("warningMessage",
                    "No re-study fees found for the selected campus and year.");
        }

        model.addAttribute("referenceCampus", referenceCampus);
        model.addAttribute("referenceTuitions", referenceTuitions);
        model.addAttribute("selectedCampusId", campusId);
        model.addAttribute("selectedYear", admissionYear);

        return "AnnualReStudyFeesByCampus";
    }
}