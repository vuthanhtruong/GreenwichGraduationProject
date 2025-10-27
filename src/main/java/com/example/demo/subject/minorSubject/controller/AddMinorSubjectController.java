package com.example.demo.subject.minorSubject.controller;

import com.example.demo.subject.minorSubject.model.MinorSubjects;
import com.example.demo.subject.minorSubject.service.MinorSubjectsService;
import com.example.demo.user.deputyStaff.service.DeputyStaffsService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/deputy-staff-home/minor-subjects-list")
@PreAuthorize("hasRole('DEPUTY_STAFF')")
public class AddMinorSubjectController {

    private final MinorSubjectsService subjectsService;
    private final DeputyStaffsService deputyStaffsService;

    public AddMinorSubjectController(MinorSubjectsService subjectsService, DeputyStaffsService deputyStaffsService) {
        this.subjectsService = subjectsService;
        this.deputyStaffsService = deputyStaffsService;
    }

    @PostMapping("/add-subject")
    public String addSubject(
            @Valid @ModelAttribute("newSubject") MinorSubjects newSubject,
            Model model,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        Map<String, String> errors = subjectsService.validateSubject(newSubject);

        if (!errors.isEmpty()) {
            model.addAttribute("openAddOverlay", true);
            model.addAttribute("errors", errors);
            model.addAttribute("newSubject", newSubject);
            model.addAttribute("subjects", subjectsService.getPaginatedSubjects(
                    0,
                    (Integer) session.getAttribute("subjectPageSize") != null ? (Integer) session.getAttribute("subjectPageSize") : 20
            ));
            model.addAttribute("currentPage", session.getAttribute("subjectPage") != null ? session.getAttribute("subjectPage") : 1);
            model.addAttribute("totalPages", session.getAttribute("subjectTotalPages") != null ? session.getAttribute("subjectTotalPages") : 1);
            model.addAttribute("pageSize", session.getAttribute("subjectPageSize") != null ? session.getAttribute("subjectPageSize") : 20);
            model.addAttribute("totalSubjects", subjectsService.numberOfSubjects());
            return "MinorSubjectsList";
        }

        try {
            String subjectId = subjectsService.generateUniqueSubjectId(
                    deputyStaffsService.getDeputyStaff().getId(),
                    LocalDate.now()
            );
            newSubject.setSubjectId(subjectId);
            newSubject.setCreator(deputyStaffsService.getDeputyStaff());

            subjectsService.addSubject(newSubject);

            redirectAttributes.addFlashAttribute("message", "Subject added successfully!");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
            return "redirect:/deputy-staff-home/minor-subjects-list";
        } catch (Exception e) {
            Map<String, String> errorsCatch = new HashMap<>();
            errorsCatch.put("general", "Error adding subject: " + e.getMessage());
            model.addAttribute("errors", errorsCatch);
            model.addAttribute("newSubject", newSubject);
            model.addAttribute("openAddOverlay", true);
            model.addAttribute("subjects", subjectsService.getPaginatedSubjects(
                    0,
                    (Integer) session.getAttribute("subjectPageSize") != null ? (Integer) session.getAttribute("subjectPageSize") : 20
            ));
            model.addAttribute("currentPage", session.getAttribute("subjectPage") != null ? session.getAttribute("subjectPage") : 1);
            model.addAttribute("totalPages", session.getAttribute("subjectTotalPages") != null ? session.getAttribute("subjectTotalPages") : 1);
            model.addAttribute("pageSize", session.getAttribute("subjectPageSize") != null ? session.getAttribute("subjectPageSize") : 20);
            model.addAttribute("totalSubjects", subjectsService.numberOfSubjects());
            return "MinorSubjectsList";
        }
    }
}