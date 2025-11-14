package com.example.demo.subject.specializedSubject.controller;

import com.example.demo.curriculum.model.Curriculum;
import com.example.demo.curriculum.service.CurriculumService;
import com.example.demo.specialization.model.Specialization;
import com.example.demo.specialization.service.SpecializationService;
import com.example.demo.user.staff.service.StaffsService;
import com.example.demo.subject.specializedSubject.model.SpecializedSubject;
import com.example.demo.subject.specializedSubject.service.SpecializedSubjectsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/staff-home/specialized-subjects-list")
@PreAuthorize("hasRole('STAFF')")
public class EditSpecializedSubjectController {

    private final SpecializedSubjectsService subjectsService;
    private final CurriculumService curriculumService;
    private final SpecializationService specializationService;
    private final StaffsService staffsService;

    @Autowired
    public EditSpecializedSubjectController(SpecializedSubjectsService subjectsService,
                                            CurriculumService curriculumService,
                                            SpecializationService specializationService, StaffsService staffsService) {
        this.subjectsService = subjectsService;
        this.curriculumService = curriculumService;
        this.specializationService = specializationService;
        this.staffsService = staffsService;
    }

    @PostMapping("/edit-specialized-subject-form")
    public String showEditSubjectForm(
            @RequestParam String id,
            @RequestParam(required = false) String specializationId,
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false, defaultValue = "list") String source,
            Model model) {
        SpecializedSubject subject = subjectsService.getSubjectById(id);
        if (subject == null) {
            if (source.equals("search")) {
                return "redirect:/staff-home/specialized-subjects-list/search-subjects?error=Subject+not+found" +
                        "&searchType=" + (searchType != null ? searchType : "") +
                        "&keyword=" + (keyword != null ? keyword : "") +
                        "&page=" + page +
                        "&pageSize=" + (pageSize != null ? pageSize : 5) +
                        "&specializationId=" + (specializationId != null ? specializationId : "");
            }
            return "redirect:/staff-home/specialized-subjects-list?error=Subject+not+found" +
                    "&page=" + page +
                    "&pageSize=" + (pageSize != null ? pageSize : 5) +
                    "&specializationId=" + (specializationId != null ? specializationId : "");
        }
        model.addAttribute("subject", subject);
        model.addAttribute("curriculums", curriculumService.getCurriculums());
        model.addAttribute("specializations", specializationService.specializationsByMajor(staffsService.getStaffMajor()));
        model.addAttribute("searchType", searchType);
        model.addAttribute("keyword", keyword);
        model.addAttribute("page", page);
        model.addAttribute("pageSize", pageSize != null ? pageSize : 5);
        model.addAttribute("source", source);
        model.addAttribute("selectedSpecializationId", specializationId);
        return "EditSpecializedSubjectForm";
    }

    @PutMapping("/edit-specialized-subject-form")
    public String updateSubject(
            @Valid @ModelAttribute("subject") SpecializedSubject subject,
            @RequestParam(value = "curriculumId") String curriculumId,
            @RequestParam(value = "specializationId") String specializationId,
            @RequestParam(value = "searchType", required = false) String searchType,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @RequestParam(value = "source", required = false, defaultValue = "list") String source,
            RedirectAttributes redirectAttributes,
            Model model) {
        Map<String, String> errors = subjectsService.validateSubject(subject, specializationId, curriculumId);
        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            model.addAttribute("curriculumId", curriculumId);
            model.addAttribute("specializationId", specializationId);
            model.addAttribute("curriculums", curriculumService.getCurriculums());
            model.addAttribute("specializations", specializationService.specializationsByMajor(staffsService.getStaffMajor()));
            model.addAttribute("searchType", searchType);
            model.addAttribute("keyword", keyword);
            model.addAttribute("page", page);
            model.addAttribute("pageSize", pageSize != null ? pageSize : 5);
            model.addAttribute("source", source);
            model.addAttribute("selectedSpecializationId", specializationId);
            return "EditSpecializedSubjectForm";
        }

        try {
            SpecializedSubject existingSubject = subjectsService.getSubjectById(subject.getSubjectId());
            if (existingSubject == null) {
                redirectAttributes.addFlashAttribute("error", "Subject with ID " + subject.getSubjectId() + " not found.");
                if (source.equals("search")) {
                    redirectAttributes.addFlashAttribute("searchType", searchType);
                    redirectAttributes.addFlashAttribute("keyword", keyword);
                    redirectAttributes.addFlashAttribute("page", page);
                    redirectAttributes.addFlashAttribute("pageSize", pageSize);
                    redirectAttributes.addFlashAttribute("specializationId", specializationId);
                    return "redirect:/staff-home/specialized-subjects-list/search-subjects";
                }
                redirectAttributes.addFlashAttribute("page", page);
                redirectAttributes.addFlashAttribute("pageSize", pageSize);
                redirectAttributes.addFlashAttribute("specializationId", specializationId);
                return "redirect:/staff-home/specialized-subjects-list";
            }
            Curriculum curriculum=curriculumService.getCurriculumById(curriculumId);
            Specialization specialization=specializationService.getSpecializationById(specializationId);
            subject.setCurriculum(curriculum);
            subject.setSpecialization(specialization);
            subjectsService.editSubject(subject.getSubjectId(), subject);
            redirectAttributes.addFlashAttribute("successMessage", "Subject updated successfully!");
            if (source.equals("search")) {
                redirectAttributes.addFlashAttribute("searchType", searchType);
                redirectAttributes.addFlashAttribute("keyword", keyword);
                redirectAttributes.addFlashAttribute("page", page);
                redirectAttributes.addFlashAttribute("pageSize", pageSize);
                redirectAttributes.addFlashAttribute("specializationId", specializationId);
                return "redirect:/staff-home/specialized-subjects-list/search-subjects";
            }
            redirectAttributes.addFlashAttribute("page", page);
            redirectAttributes.addFlashAttribute("pageSize", pageSize);
            redirectAttributes.addFlashAttribute("specializationId", specializationId);
            return "redirect:/staff-home/specialized-subjects-list";
        } catch (Exception e) {
            Map<String, String> errorsCatch = new HashMap<>();
            errorsCatch.put("general", "Error updating subject: " + e.getMessage());
            model.addAttribute("errors", errorsCatch);
            model.addAttribute("curriculums", curriculumService.getCurriculums());
            model.addAttribute("specializations", specializationService.specializationsByMajor(subject.getSpecialization().getMajor()));
            model.addAttribute("searchType", searchType);
            model.addAttribute("keyword", keyword);
            model.addAttribute("page", page);
            model.addAttribute("pageSize", pageSize != null ? pageSize : 5);
            model.addAttribute("source", source);
            model.addAttribute("selectedSpecializationId", specializationId);
            return "EditSpecializedSubjectForm";
        }
    }
}