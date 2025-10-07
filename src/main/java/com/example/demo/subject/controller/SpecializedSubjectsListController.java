package com.example.demo.subject.controller;

import com.example.demo.Curriculum.service.CurriculumService;
import com.example.demo.Specialization.service.SpecializationService;
import com.example.demo.entity.Enums.LearningProgramTypes;
import com.example.demo.subject.model.SpecializedSubject;
import com.example.demo.staff.service.StaffsService;
import com.example.demo.subject.service.SpecializedSubjectsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/staff-home/specialized-subjects-list")
@PreAuthorize("hasRole('STAFF')")
public class SpecializedSubjectsListController {

    private final SpecializedSubjectsService subjectsService;
    private final StaffsService staffsService;
    private final CurriculumService curriculumService;
    private final SpecializationService specializationService;

    @Autowired
    public SpecializedSubjectsListController(SpecializedSubjectsService subjectsService, StaffsService staffsService,
                                             CurriculumService curriculumService, SpecializationService specializationService) {
        this.subjectsService = subjectsService;
        this.staffsService = staffsService;
        this.curriculumService = curriculumService;
        this.specializationService = specializationService;
    }

    @GetMapping("")
    public String showSubjectsList(
            Model model,
            HttpSession session,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) String specializationId) {
        try {
            if (pageSize == null) {
                pageSize = (Integer) session.getAttribute("specializedSubjectPageSize");
                if (pageSize == null) {
                    pageSize = 20;
                }
            }
            session.setAttribute("specializedSubjectPageSize", pageSize);

            // Get specialization based on parameter
            var specialization = specializationId != null && !specializationId.isEmpty()
                    ? specializationService.getSpecializationById(specializationId)
                    : null;

            // Get specializations for dropdown
            model.addAttribute("specializations", specializationService.specializationsByMajor(staffsService.getStaffMajor()));

            if (specialization == null) {
                model.addAttribute("subjects", new ArrayList<>());
                model.addAttribute("newSubject", new SpecializedSubject());
                model.addAttribute("currentPage", 1);
                model.addAttribute("totalPages", 1);
                model.addAttribute("pageSize", pageSize);
                model.addAttribute("totalSubjects", 0);
                model.addAttribute("message", "Please select a specialization to view subjects.");
                model.addAttribute("alertClass", "alert-warning");
                model.addAttribute("learningProgramTypes", LearningProgramTypes.values());
                model.addAttribute("curriculums", curriculumService.getCurriculums());
                return "SpecializedSubjectsList";
            }

            Long totalSubjects = subjectsService.numberOfSubjects(specialization);
            int totalPages = Math.max(1, (int) Math.ceil((double) totalSubjects / pageSize));
            page = Math.max(1, Math.min(page, totalPages));
            session.setAttribute("specializedSubjectPage", page);
            session.setAttribute("specializedSubjectTotalPages", totalPages);

            if (totalSubjects == 0) {
                model.addAttribute("subjects", new ArrayList<>());
                model.addAttribute("newSubject", new SpecializedSubject());
                model.addAttribute("currentPage", 1);
                model.addAttribute("totalPages", 1);
                model.addAttribute("pageSize", pageSize);
                model.addAttribute("totalSubjects", 0);
                model.addAttribute("message", "No specialized subjects found for this specialization.");
                model.addAttribute("alertClass", "alert-warning");
                model.addAttribute("learningProgramTypes", LearningProgramTypes.values());
                model.addAttribute("curriculums", curriculumService.getCurriculums());
                model.addAttribute("selectedSpecializationId", specialization.getSpecializationId());
                return "SpecializedSubjectsList";
            }

            int firstResult = (page - 1) * pageSize;
            List<SpecializedSubject> subjects = subjectsService.getPaginatedSubjects(firstResult, pageSize, specialization);

            model.addAttribute("subjects", subjects);
            model.addAttribute("newSubject", new SpecializedSubject());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("totalSubjects", totalSubjects);
            model.addAttribute("learningProgramTypes", LearningProgramTypes.values());
            model.addAttribute("curriculums", curriculumService.getCurriculums());
            model.addAttribute("selectedSpecializationId", specialization.getSpecializationId());
            return "SpecializedSubjectsList";
        } catch (Exception e) {
            model.addAttribute("errors", List.of("An error occurred while retrieving specialized subjects: " + e.getMessage()));
            model.addAttribute("newSubject", new SpecializedSubject());
            model.addAttribute("currentPage", 1);
            model.addAttribute("totalPages", 1);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("totalSubjects", 0);
            model.addAttribute("learningProgramTypes", LearningProgramTypes.values());
            model.addAttribute("curriculums", curriculumService.getCurriculums());
            model.addAttribute("specializations", specializationService.specializationsByMajor(staffsService.getStaffMajor()));
            return "SpecializedSubjectsList";
        }
    }
}