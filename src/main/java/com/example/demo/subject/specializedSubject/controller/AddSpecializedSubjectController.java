package com.example.demo.subject.specializedSubject.controller;

import com.example.demo.curriculum.model.Curriculum;
import com.example.demo.curriculum.service.CurriculumService;
import com.example.demo.Specialization.model.Specialization;
import com.example.demo.Specialization.service.SpecializationService;
import com.example.demo.subject.specializedSubject.model.SpecializedSubject;
import com.example.demo.user.staff.service.StaffsService;
import com.example.demo.subject.specializedSubject.service.SpecializedSubjectsService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/staff-home/specialized-subjects-list")
@PreAuthorize("hasRole('STAFF')")
public class AddSpecializedSubjectController {

    private final SpecializedSubjectsService subjectsService;
    private final StaffsService staffsService;
    private final CurriculumService curriculumService;
    private final SpecializationService specializationService;

    @Autowired
    public AddSpecializedSubjectController(SpecializedSubjectsService subjectsService, StaffsService staffsService,
                                           CurriculumService curriculumService, SpecializationService specializationService) {
        this.subjectsService = subjectsService;
        this.staffsService = staffsService;
        this.curriculumService = curriculumService;
        this.specializationService = specializationService;
    }

    @PostMapping("/add-subject")
    public String addSubject(
            @Valid @ModelAttribute("newSubject") SpecializedSubject newSubject,
            @RequestParam(value = "curriculumId", required = false) String curriculumId,
            @RequestParam(value = "specializationId", required = false) String specializationId,
            Model model,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        Map<String, String> errors = subjectsService.validateSubject(newSubject, specializationId, curriculumId);

        if (!errors.isEmpty()) {
            model.addAttribute("openAddOverlay", true);
            model.addAttribute("errors", errors);
            model.addAttribute("newSubject", newSubject);
            model.addAttribute("curriculums", curriculumService.getCurriculums());
            model.addAttribute("specializations", specializationService.specializationsByMajor(staffsService.getStaffMajor()));

            // Restore pagination attributes
            int pageSize = (Integer) session.getAttribute("specializedSubjectPageSize") != null
                    ? (Integer) session.getAttribute("specializedSubjectPageSize") : 20;
            int page = (Integer) session.getAttribute("specializedSubjectPage") != null
                    ? (Integer) session.getAttribute("specializedSubjectPage") : 1;
            Long totalSubjects = subjectsService.numberOfSubjects(staffsService.getStaffMajor());
            int totalPages = Math.max(1, (int) Math.ceil((double) totalSubjects / pageSize));
            int firstResult = (page - 1) * pageSize;

            List<SpecializedSubject> subjects = subjectsService.getPaginatedSubjects(firstResult, pageSize);
            model.addAttribute("subjects", subjects);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("totalSubjects", totalSubjects);
            return "SpecializedSubjectsList";
        }

        try {
            String subjectId = subjectsService.generateUniqueSubjectId(specializationId, LocalDate.now());
            newSubject.setSubjectId(subjectId);
            newSubject.setCreator(staffsService.getStaff());
            Specialization specialization = specializationService.getSpecializationById(specializationId);
            Curriculum curriculum = curriculumService.getCurriculumById(curriculumId);
            newSubject.setSpecialization(specialization);
            newSubject.setCurriculum(curriculum);

            subjectsService.addSubject(newSubject, specialization);

            redirectAttributes.addFlashAttribute("message", "Specialized subject added successfully!");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
            return "redirect:/staff-home/specialized-subjects-list";
        } catch (Exception e) {
            Map<String, String> errorsCatch = new HashMap<>();
            errorsCatch.put("general", "Error adding specialized subject: " + e.getMessage());
            model.addAttribute("errors", errorsCatch);
            model.addAttribute("newSubject", newSubject);
            model.addAttribute("openAddOverlay", true);
            model.addAttribute("curriculums", curriculumService.getCurriculums());
            model.addAttribute("specializations", specializationService.specializationsByMajor(staffsService.getStaffMajor()));

            // Restore pagination attributes
            int pageSize = (Integer) session.getAttribute("specializedSubjectPageSize") != null
                    ? (Integer) session.getAttribute("specializedSubjectPageSize") : 20;
            int page = (Integer) session.getAttribute("specializedSubjectPage") != null
                    ? (Integer) session.getAttribute("specializedSubjectPage") : 1;
            Long totalSubjects = subjectsService.numberOfSubjects(staffsService.getStaffMajor());
            int totalPages = Math.max(1, (int) Math.ceil((double) totalSubjects / pageSize));
            int firstResult = (page - 1) * pageSize;

            List<SpecializedSubject> subjects = subjectsService.getPaginatedSubjects(firstResult, pageSize);
            model.addAttribute("subjects", subjects);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("totalSubjects", totalSubjects);
            return "SpecializedSubjectsList";
        }
    }
}