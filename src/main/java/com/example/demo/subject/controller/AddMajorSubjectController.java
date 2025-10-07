package com.example.demo.subject.controller;

import com.example.demo.Curriculum.model.Curriculum;
import com.example.demo.Curriculum.service.CurriculumService;
import com.example.demo.staff.service.StaffsService;
import com.example.demo.subject.model.MajorSubjects;
import com.example.demo.subject.service.MajorSubjectsService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/staff-home/major-subjects-list")
@PreAuthorize("hasRole('STAFF')")
public class AddMajorSubjectController {

    private final MajorSubjectsService subjectsService;
    private final StaffsService staffsService;
    private final CurriculumService curriculumService;

    public AddMajorSubjectController(MajorSubjectsService subjectsService, StaffsService staffsService, CurriculumService curriculumService) {
        this.subjectsService = subjectsService;
        this.staffsService = staffsService;
        this.curriculumService = curriculumService;
    }

    @PostMapping("/add-subject")
    public String addSubject(
            @Valid @ModelAttribute("newSubject") MajorSubjects newSubject,
            @RequestParam(value = "curriculumId", required = false) String curriculumId,
            Model model,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        Map<String, String> errors = subjectsService.validateSubject(newSubject, curriculumId);

        if (!errors.isEmpty()) {
            model.addAttribute("openAddOverlay", true);
            model.addAttribute("errors", errors);
            model.addAttribute("newSubject", newSubject);
            model.addAttribute("curriculums", curriculumService.getCurriculums());
            model.addAttribute("subjects", subjectsService.getPaginatedSubjects(
                    0,
                    (Integer) session.getAttribute("subjectPageSize") != null ? (Integer) session.getAttribute("subjectPageSize") : 20,
                    staffsService.getStaffMajor()
            ));
            model.addAttribute("currentPage", session.getAttribute("subjectPage") != null ? session.getAttribute("subjectPage") : 1);
            model.addAttribute("totalPages", session.getAttribute("subjectTotalPages") != null ? session.getAttribute("subjectTotalPages") : 1);
            model.addAttribute("pageSize", session.getAttribute("subjectPageSize") != null ? session.getAttribute("subjectPageSize") : 20);
            model.addAttribute("totalSubjects", subjectsService.numberOfSubjects(staffsService.getStaffMajor()));
            return "MajorSubjectsList";
        }

        try {
            String subjectId = subjectsService.generateUniqueSubjectId(
                    staffsService.getStaffMajor().getMajorId(),
                    LocalDate.now()
            );
            newSubject.setSubjectId(subjectId);
            newSubject.setCreator(staffsService.getStaff());
            newSubject.setMajor(staffsService.getStaffMajor());

            Curriculum curriculum = curriculumService.getCurriculumById(curriculumId);
            newSubject.setCurriculum(curriculum);

            subjectsService.addSubject(newSubject);

            redirectAttributes.addFlashAttribute("message", "Subject added successfully!");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
            return "redirect:/staff-home/major-subjects-list";
        } catch (Exception e) {
            Map<String, String> errorsCatch = new HashMap<>();
            errorsCatch.put("general", "Error adding subject: " + e.getMessage());
            model.addAttribute("errors", errorsCatch);
            model.addAttribute("newSubject", newSubject);
            model.addAttribute("openAddOverlay", true);
            model.addAttribute("curriculums", curriculumService.getCurriculums());
            model.addAttribute("subjects", subjectsService.getPaginatedSubjects(
                    0,
                    (Integer) session.getAttribute("subjectPageSize") != null ? (Integer) session.getAttribute("subjectPageSize") : 20,
                    staffsService.getStaffMajor()
            ));
            model.addAttribute("currentPage", session.getAttribute("subjectPage") != null ? session.getAttribute("subjectPage") : 1);
            model.addAttribute("totalPages", session.getAttribute("subjectTotalPages") != null ? session.getAttribute("subjectTotalPages") : 1);
            model.addAttribute("pageSize", session.getAttribute("subjectPageSize") != null ? session.getAttribute("subjectPageSize") : 20);
            model.addAttribute("totalSubjects", subjectsService.numberOfSubjects(staffsService.getStaffMajor()));
            return "MajorSubjectsList";
        }
    }
}